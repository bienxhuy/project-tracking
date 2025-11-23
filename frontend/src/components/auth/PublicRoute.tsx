import { Navigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { UserRole } from "@/types/user.type";

interface PublicRouteProps {
  children: React.ReactNode;
}

/**
 * PublicRoute - Redirects to default route based on role if user is already authenticated
 * Used for login/register pages
 */
export function PublicRoute({ children }: PublicRouteProps) {
  const { isAuthenticated, isLoading, user } = useAuth();

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

  // Helper function to get default route based on role
  const getDefaultRouteByRole = (role: UserRole): string => {
    switch (role) {
      case UserRole.STUDENT:
        return "/test-api";
      case UserRole.INSTRUCTOR:
        return "/temp-page";
      case UserRole.ADMIN:
        return "/admin";
      default:
        return "/";
    }
  };

  // Redirect to default route based on role if already authenticated
  if (isAuthenticated && user?.role) {
    const defaultRoute = getDefaultRouteByRole(user.role);
    return <Navigate to={defaultRoute} replace />;
  }

  // Render public content (login/register pages)
  return <>{children}</>;
}

