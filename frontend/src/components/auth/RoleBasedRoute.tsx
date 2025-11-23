import { Navigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { UserRole } from "@/types/user.type";

interface RoleBasedRouteProps {
  children: React.ReactNode;
  allowedRoles: UserRole[];
  redirectTo?: string;
}

/**
 * RoleBasedRoute - Protects routes based on user roles
 * Only allows access if user has one of the specified roles
 */
export function RoleBasedRoute({ 
  children, 
  allowedRoles,
  redirectTo = "/" 
}: RoleBasedRouteProps) {
  const { isAuthenticated, isLoading, user, hasRole } = useAuth();
  
  // Check for access token in localStorage
  const token = localStorage.getItem('accessToken');

  // Show loading state while checking authentication
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Đang tải...</p>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!token || !isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has one of the allowed roles
  const hasAllowedRole = allowedRoles.some(role => hasRole(role));

  if (!hasAllowedRole) {
    // User is authenticated but doesn't have required role
    // Redirect to home page or show access denied message
    return <Navigate to={redirectTo} replace />;
  }

  // Render protected content
  return <>{children}</>;
}

