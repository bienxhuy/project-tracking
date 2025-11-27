import { Link } from 'react-router-dom';
import { Bell, Home } from 'lucide-react';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { UserRole } from '@/types/user.type';

export default function HomePage() {
  const { user, isAuthenticated, isLoading } = useAuth();
  const navigate = useNavigate();

  // Helper function to get default route based on role
  const getDefaultRouteByRole = (role: UserRole): string => {
    switch (role) {
      case UserRole.STUDENT:
        return "/student/dashboard";
      case UserRole.INSTRUCTOR:
        return "/instructor/dashboard";
      case UserRole.ADMIN:
        return "/admin";
      default:
        return "/";
    }
  };

  // Auto-navigate based on role when component mounts or user changes
  useEffect(() => {
    if (!isLoading && isAuthenticated && user?.role) {
      const defaultRoute = getDefaultRouteByRole(user.role);
      navigate(defaultRoute, { replace: true });
    }
  }, [user, isAuthenticated, isLoading, navigate]);

  // Show loading while checking authentication or navigating
  if (isLoading || !isAuthenticated || !user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Đang tải...</p>
        </div>
      </div>
    );
  }

  // This should rarely be seen as navigation happens quickly
  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p className="mt-4 text-gray-600">Đang chuyển hướng...</p>
      </div>
    </div>
  );
}
