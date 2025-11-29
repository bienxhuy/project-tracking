import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import { 
  Users, 
  Settings, 
  LayoutDashboard,
  ChevronLeft,
  Menu,
  LogOut
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAdminSidebar } from "@/contexts/AdminSidebarContext";
import { useAuth } from "@/contexts/AuthContext";
import { useToast } from "@/components/ui/toast";

interface NavItem {
  title: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  disabled?: boolean;
}

const navItems: NavItem[] = [
  {
    title: "Dashboard",
    href: "/admin/dashboard",
    icon: LayoutDashboard,
  },
  {
    title: "Users",
    href: "/admin/users",
    icon: Users,
  },
  {
    title: "Settings",
    href: "/admin/settings",
    icon: Settings,
    disabled: true, // Coming soon
  },
];

export function AdminSidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { collapsed, setCollapsed, mobileOpen, setMobileOpen } = useAdminSidebar();
  const { logout } = useAuth();
  const { addToast } = useToast();
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  const handleLogout = async () => {
    if (isLoggingOut) return;
    setIsLoggingOut(true);

    try {
      await logout();
      addToast({
        title: "Signed out",
        description: "You have been logged out of the admin panel.",
        variant: "success",
      });
      navigate("/login", { replace: true });
    } catch (error) {
      console.error("Logout failed:", error);
      addToast({
        title: "Logout failed",
        description: "Something went wrong. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoggingOut(false);
      setMobileOpen(false);
    }
  };

  return (
    <>
      {/* Mobile Menu Button */}
      <Button
        variant="ghost"
        size="icon"
        className="fixed top-4 left-4 z-50 lg:hidden"
        onClick={() => setMobileOpen(!mobileOpen)}
      >
        <Menu className="h-5 w-5" />
      </Button>

      {/* Mobile Overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          "fixed left-0 top-0 z-40 h-screen bg-white border-r border-gray-200 transition-all duration-300",
          collapsed ? "w-16" : "w-64",
          mobileOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"
        )}
      >
        <div className="flex flex-col h-full">
          {/* Logo/Header */}
          <div className={cn(
            "h-16 border-b border-gray-200 flex items-center px-4",
            collapsed ? "justify-center" : "justify-between"
          )}>
            {!collapsed ? (
              <>
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                    <span className="text-white font-bold text-sm">A</span>
                  </div>
                  <span className="font-semibold text-gray-900">Admin Panel</span>
                </div>
                
                {/* Collapse Button (Desktop only) */}
                <Button
                  variant="ghost"
                  size="icon"
                  className="hidden lg:flex"
                  onClick={() => setCollapsed(!collapsed)}
                >
                  <ChevronLeft className="h-4 w-4 transition-transform" />
                </Button>
              </>
            ) : (
              /* Collapsed view - show icon and toggle button stacked or just toggle */
              <Button
                variant="ghost"
                size="icon"
                className="hidden lg:flex"
                onClick={() => setCollapsed(!collapsed)}
              >
                <ChevronLeft className="h-4 w-4 transition-transform rotate-180" />
              </Button>
            )}
          </div>

          {/* Navigation */}
          <nav className="flex-1 overflow-y-auto py-4">
            <ul className="space-y-1 px-2">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.href;
                const isDisabled = item.disabled;

                if (isDisabled) {
                  return (
                    <li key={item.href}>
                      <div
                        className={cn(
                          "flex items-center gap-3 rounded-lg px-3 py-2 text-gray-400 cursor-not-allowed",
                          collapsed ? "justify-center" : ""
                        )}
                        title={collapsed ? item.title : undefined}
                      >
                        <Icon className="h-5 w-5 flex-shrink-0" />
                        {!collapsed && (
                          <div className="flex items-center justify-between flex-1">
                            <span className="text-sm font-medium">{item.title}</span>
                            <span className="text-xs bg-gray-100 px-2 py-0.5 rounded">
                              Soon
                            </span>
                          </div>
                        )}
                      </div>
                    </li>
                  );
                }

                return (
                  <li key={item.href}>
                    <Link
                      to={item.href}
                      onClick={() => setMobileOpen(false)}
                      className={cn(
                        "flex items-center gap-3 rounded-lg px-3 py-2 transition-colors",
                        collapsed ? "justify-center" : "",
                        isActive
                          ? "bg-blue-50 text-blue-600"
                          : "text-gray-700 hover:bg-gray-100"
                      )}
                      title={collapsed ? item.title : undefined}
                    >
                      <Icon className="h-5 w-5 flex-shrink-0" />
                      {!collapsed && (
                        <span className="text-sm font-medium">{item.title}</span>
                      )}
                      {!collapsed && item.badge && (
                        <span className="ml-auto text-xs bg-blue-600 text-white px-2 py-0.5 rounded-full">
                          {item.badge}
                        </span>
                      )}
                    </Link>
                  </li>
                );
              })}
            </ul>

            {/* Logout Button */}
            <div className="px-2 mt-2 pt-2 border-t border-gray-200">
              <button
                onClick={handleLogout}
                className={cn(
                  "w-full flex items-center gap-3 rounded-lg px-3 py-2 transition-colors text-red-600 hover:bg-red-50",
                  collapsed ? "justify-center" : ""
                )}
                title={collapsed ? "Logout" : undefined}
                disabled={isLoggingOut}
              >
                <LogOut className="h-5 w-5 flex-shrink-0" />
                {!collapsed && (
                  <span className="text-sm font-medium">
                    {isLoggingOut ? "Signing out..." : "Logout"}
                  </span>
                )}
              </button>
            </div>
          </nav>

          {/* Footer */}
          {!collapsed && (
            <div className="border-t border-gray-200 p-4">
              <div className="text-xs text-gray-500">
                <p className="font-medium">Admin Mode</p>
                <p className="mt-1">Manage system settings</p>
              </div>
            </div>
          )}
        </div>
      </aside>
    </>
  );
}

