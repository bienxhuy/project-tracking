import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User } from '../types/user.type.ts';
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
    try {
      const response = await authService.login(credentials);
      
      if (response.errorCode === 'VERIFYING_EMAIL') {
        throw new Error('Please verify your email before logging in');
      }

      if (response.errorCode === 'UNAUTHORIZED') {
        throw new Error('Your account has been banned');
      }

      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
        
        await loadUser();
      }
    } catch (error: any) {
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

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        isLoading,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
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