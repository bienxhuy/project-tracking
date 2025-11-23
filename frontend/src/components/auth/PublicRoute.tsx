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
  const { isAuthenticated, isLoading } = useAuth();

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

  // Redirect to HomePage if already authenticated (HomePage will handle role routing)
  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  // Render public content (login/register pages)
  return <>{children}</>;
}

