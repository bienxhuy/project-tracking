import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { UserRole } from "@/types/user.type";

export default function LoginPage() {
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<{ identifier?: string; password?: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  const { login, user, isAuthenticated } = useAuth();
  const navigate = useNavigate();

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

  // Navigate when user is loaded and authenticated after successful login
  useEffect(() => {
    // Only navigate if user is authenticated and has a role
    // This ensures we don't navigate during initial load
    if (isAuthenticated && user?.role) {
      const defaultRoute = getDefaultRouteByRole(user.role);
      navigate(defaultRoute, { replace: true });
    }
  }, [isAuthenticated, user, navigate]);

  const validateForm = () => {
    const newErrors: { identifier?: string; password?: string } = {};
    
    if (!identifier.trim()) {
      newErrors.identifier = "Tên đăng nhập là bắt buộc";
    }
    
    if (!password.trim()) {
      newErrors.password = "Mật khẩu là bắt buộc";
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsLoading(true);
    // Clear previous errors
    setErrors({});
    
    try {
      await login({ identifier, password });
      // Don't navigate here - let useEffect handle it after user is loaded
    } catch (error: any) {
      // Check if it's a 401 or BAD_CREDENTIALS error
      const isBadCredentials = 
        error.response?.status === 401 || 
        error.response?.data?.errorCode === 'BAD_CREDENTIALS' ||
        error.message?.includes('Bad credentials') ||
        error.message?.includes('Authentication failed');
      
      if (isBadCredentials) {
        // Show error on both fields
        setErrors({
          identifier: "Thông tin đăng nhập không chính xác",
          password: "Thông tin đăng nhập không chính xác",
        });
      } else {
        // Other errors (account banned, email verification, etc.)
        setErrors({
          identifier: error.message || "Đăng nhập thất bại",
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    // TODO: Implement Google OAuth login
    console.log("Google login clicked");
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <p className="text-lg font-semibold text-blue-900">
            TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP.HCM
          </p>
        </div>

        {/* Login Form */}
        <div className="bg-white rounded-lg shadow-lg p-8">
          {/* Title */}
          <h2 className="text-3xl font-bold text-blue-900 mb-2 text-center">
            ĐĂNG NHẬP
          </h2>

          <form onSubmit={handleSubmit} className="space-y-6 mt-8">
            {/* Username Field */}
            <div>
              <label 
                htmlFor="identifier" 
                className={`block text-sm font-medium mb-2 ${
                  errors.identifier ? "text-red-600" : "text-gray-700"
                }`}
              >
                Tên đăng nhập
              </label>
              <Input
                id="identifier"
                type="text"
                value={identifier}
                onChange={(e) => {
                  setIdentifier(e.target.value);
                  // Clear both errors when user starts typing
                  if (errors.identifier || errors.password) {
                    setErrors({});
                  }
                }}
                className={`w-full ${
                  errors.identifier 
                    ? "border-red-600 focus-visible:ring-red-600 focus-visible:border-red-600" 
                    : ""
                }`}
                placeholder="Nhập tên đăng nhập"
              />
              {errors.identifier && (
                <p className="text-sm text-red-600 mt-1">
                  {errors.identifier}
                </p>
              )}
            </div>

            {/* Password Field */}
            <div>
              <label 
                htmlFor="password" 
                className={`block text-sm font-medium mb-2 ${
                  errors.password ? "text-red-600" : "text-gray-700"
                }`}
              >
                Mật khẩu
              </label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  // Clear both errors when user starts typing
                  if (errors.identifier || errors.password) {
                    setErrors({});
                  }
                }}
                className={`w-full ${
                  errors.password 
                    ? "border-red-600 focus-visible:ring-red-600 focus-visible:border-red-600" 
                    : ""
                }`}
                placeholder="Nhập mật khẩu"
              />
              {errors.password && (
                <p className="text-sm text-red-600 mt-1">
                  {errors.password}
                </p>
              )}
            </div>

            {/* Login Button */}
            <Button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white"
              disabled={isLoading}
            >
              {isLoading ? "Đang đăng nhập..." : "Đăng nhập"}
            </Button>

            {/* Google Login Button */}
            <Button
              type="button"
              variant="outline"
              onClick={handleGoogleLogin}
              className="w-full border-blue-300 text-gray-700 hover:bg-gray-50"
            >
              <svg
                className="w-5 h-5 mr-2"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  fill="#34A853"
                />
                <path
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  fill="#EA4335"
                />
              </svg>
              Đăng nhập với Google
            </Button>
          </form>

          {/* Forgot Password Link */}
          <div className="mt-6 text-center">
            <a
              href="#"
              className="text-sm text-gray-600 hover:text-gray-800 underline"
              onClick={(e) => {
                e.preventDefault();
                // TODO: Implement forgot password functionality
                console.log("Forgot password clicked");
              }}
            >
              Quên mật khẩu
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

