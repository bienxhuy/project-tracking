import { createContext, useContext, useState, useEffect, ReactNode, useMemo } from 'react';
import { User, UserRole } from '../types/user.type.ts';
import { LoginRequest, RegisterRequest, AuthContextType } from '../types/auth.type.ts';
import { authService } from '../services/auth.service.ts';
import { UserStatus } from '../types/util.type.ts';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const isAuthenticated = !!user && user.accountStatus === UserStatus.ACTIVE;

  useEffect(() => {
    loadUser();
  }, []);

  const loadUser = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        setIsLoading(false);
        return;
      }

      const response = await authService.getAccount();
      setUser(response.data);
    } catch (error) {
      console.error('Failed to load user:', error);
      localStorage.removeItem('accessToken');
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  };

  const login = async (credentials: LoginRequest) => {
    // Clear any existing token before attempting login
    localStorage.removeItem('accessToken');
    setUser(null);
    
    try {
      const response = await authService.login(credentials);
      
      // Check for error codes first - if any error exists, don't save token
      if (response.errorCode) {
        localStorage.removeItem('accessToken');
        setUser(null);
        
        if (response.errorCode === 'VERIFYING_EMAIL') {
          throw new Error('Please verify your email before logging in');
        }
        if (response.errorCode === 'UNAUTHORIZED' || response.errorCode === 'ACCOUNT_INACTIVE') {
          throw new Error('Your account has been banned');
        }
        // Any other errorCode
        throw new Error(response.message || 'Login failed');
      }

      // Verify we have a valid accessToken before saving
      if (!response.data?.accessToken) {
        localStorage.removeItem('accessToken');
        setUser(null);
        throw new Error('No access token received from server');
      }

      // Only save token when login is completely successful (no errorCode and has accessToken)
      localStorage.setItem('accessToken', response.data.accessToken);
      await loadUser();
    } catch (error: any) {
      // CRITICAL: Always clear token and user on any error
      localStorage.removeItem('accessToken');
      setUser(null);
      
      console.error('Login failed:', error);
      throw error;
    }
  };

  const register = async (userData: RegisterRequest) => {
    try {
      await authService.register(userData);
      // Note: User needs to verify email before logging in
    } catch (error: any) {
      console.error('Registration failed:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      localStorage.removeItem('accessToken');
      setUser(null);
    }
  };

  const refreshUser = async () => {
    await loadUser();
  };

  // Role helper functions
  const hasRole = (role: UserRole): boolean => {
    return isAuthenticated && user?.role === role;
  };

  const isAdmin = (): boolean => {
    return hasRole(UserRole.ADMIN);
  };

  const isInstructor = (): boolean => {
    return hasRole(UserRole.INSTRUCTOR);
  };

  const isStudent = (): boolean => {
    return hasRole(UserRole.STUDENT);
  };

  // Memoize context value to prevent unnecessary re-renders
  const contextValue = useMemo<AuthContextType>(
    () => ({
      user,
      isAuthenticated,
      isLoading,
      login,
      register,
      logout,
      refreshUser,
      hasRole,
      isAdmin,
      isInstructor,
      isStudent,
    }),
    [user, isAuthenticated, isLoading]
  );

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}