import { Outlet } from "react-router-dom";
import { AdminSidebar } from "@/components/admin/AdminSidebar";
import { AdminSidebarProvider, useAdminSidebar } from "@/contexts/AdminSidebarContext";
import { cn } from "@/lib/utils";
import logo from "@/assets/logo.png"

function AdminLayoutContent() {
  const { collapsed } = useAdminSidebar();
  
  return (
    <div className="min-h-screen bg-gray-50">
      <AdminSidebar />
      
      {/* Main Content - offset by sidebar width */}
      <div className={cn(
        "transition-all duration-300",
        collapsed ? "lg:pl-16" : "lg:pl-64"
      )}>
        {/* Top Bar */}
        <header className="h-16 bg-white border-b border-gray-200 flex items-center px-6">
          <div className="flex items-center justify-between w-full">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-gradient-primary rounded-lg flex items-center justify-center cursor-pointer">
                <img src={logo} alt="Logo" />
              </div>
              <h1 className="text-xl font-extrabold text-balance tracking-tight cursor-pointer">UTEPs</h1>
            </div>
          </div>
            <div className="lg:ml-0 ml-12">
              <h1 className="text-xl font-semibold text-gray-900">Admin Panel</h1>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export function AdminLayout() {
  return (
    <AdminSidebarProvider>
      <AdminLayoutContent />
    </AdminSidebarProvider>
  );
}


