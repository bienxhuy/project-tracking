import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

import { Bell, Menu, Wifi, WifiOff } from "lucide-react"
import logo from "@/assets/logo.png"
import developing from "@/assets/developing.jpg"
import { useState, useEffect } from "react";
import { useToast } from "@/components/ui/toast";
import { useAuth } from "@/contexts/AuthContext";
import { useWebSocketNotifications } from "@/hooks/useWebSocketNotifications";
import { NotificationPermissionDialog } from "@/components/NotificationPermissionDialog";
import { requestNotificationPermission } from "@/services/firebase.service";
import { registerDeviceToken } from "@/services/notification.api";

import { ChangePasswordDialog } from "@/components/auth/ChangePasswordDialog";
import { useNavigate } from "react-router-dom";

// Notification displaying types
const notificationTypes = [
  { id: "all", title: "Tất cả" },
  { id: "unread", title: "Chưa đọc" },
  { id: "group", title: "Dự án" },
]

export const Header = () => {
  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const [showNotificationDialog, setShowNotificationDialog] = useState(false);
  const [notificationDropdownOpen, setNotificationDropdownOpen] = useState(false);
  const { logout, user } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();
  
  // WebSocket notifications
  const {
    notifications,
    unreadCount,
    isConnected,
    isLoading,
    markAsRead,
  } = useWebSocketNotifications();

  // Check notification permission on mount
  useEffect(() => {
    if ('Notification' in window) {
      const currentPermission = Notification.permission;
      console.log('Current notification permission:', currentPermission);
      
      // If permission granted, try to get token and register if not already done
      if (currentPermission === 'granted') {
        const existingToken = localStorage.getItem('fcmToken');
        if (!existingToken) {
          console.log('Permission granted but no token found, requesting token...');
          handleRequestPermission().catch(err => {
            console.error('Failed to auto-register token:', err);
          });
        }
      }
    }
  }, []);

  // Handle notification permission request
  const handleRequestPermission = async () => {
    try {
      const token = await requestNotificationPermission();
      
      if (token) {
        // Register token with backend
        await registerDeviceToken(token, 'WEB');
        console.log('Device token registered successfully');
        
        // Store token in localStorage
        localStorage.setItem('fcmToken', token);
        
        addToast({
          title: "Đã bật thông báo",
          description: "Bạn sẽ nhận được thông báo từ hệ thống",
          variant: "success",
        });
      }
    } catch (error) {
      console.error('Failed to request notification permission:', error);
      addToast({
        title: "Không thể bật thông báo",
        description: "Vui lòng kiểm tra cài đặt trình duyệt",
        variant: "destructive",
      });
    }
  };

  // Handle bell icon click - check permission before opening dropdown
  const handleBellClick = () => {
    console.log('🔔 Bell icon clicked');
    
    if (!('Notification' in window)) {
      console.warn('Browser does not support notifications');
      addToast({
        title: "Trình duyệt không hỗ trợ",
        description: "Trình duyệt của bạn không hỗ trợ thông báo",
        variant: "destructive",
      });
      return;
    }

    const currentPermission = Notification.permission;
    console.log('Current notification permission:', currentPermission);
    
    // If permission not granted and not skipped, show dialog
    if (currentPermission === 'default') {
      console.log('Showing permission dialog...');
      setShowNotificationDialog(true);
    } else if (currentPermission === 'denied') {
      console.log('Permission denied, showing error toast');
      addToast({
        title: "Thông báo đã bị chặn",
        description: "Vui lòng bật thông báo trong cài đặt trình duyệt",
        variant: "destructive",
      });
    } else {
      // Permission granted, open dropdown normally
      console.log('Permission already granted, opening dropdown');
      setNotificationDropdownOpen(true);
    }
  };

  // Mark notification as read
  const handleNotificationClick = (notificationId: number) => {
    // Call API to mark as read (handled in useWebSocketNotifications hook)
    markAsRead(notificationId);
  };

  const handleLogout = async () => {
    try {
      await logout();
      addToast({
        title: "Đăng xuất thành công",
        variant: "success",
      });
    } catch (error: any) {
      addToast({
        title: "Đăng xuất thất bại",
        description:
          error?.response?.data?.message ||
          error?.message ||
          "Không thể đăng xuất. Vui lòng thử lại.",
        variant: "destructive",
      });
    }
  };

  return (
    <>
      {/* Notification Permission Dialog */}
      <NotificationPermissionDialog
        open={showNotificationDialog}
        onOpenChange={setShowNotificationDialog}
        onRequestPermission={async () => {
          await handleRequestPermission();
          setShowNotificationDialog(false);
          // After granting permission, open the dropdown
          setNotificationDropdownOpen(true);
        }}
      />

      <header className="sticky top-0 z-50 w-full border-b border-border bg-card/95 backdrop-blur supports-[backdrop-filter]:bg-card/60">
        <div className="flex h-15 items-center justify-between px-5 md:px-15 lg:px-32">

          {/*Logo*/}
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-gradient-primary rounded-lg flex items-center justify-center cursor-pointer" onClick={() => navigate("/")}>
                <img src={logo} alt="Logo" />
              </div>
              <h1 className="text-xl font-extrabold text-balance tracking-tight cursor-pointer" onClick={() => navigate("/")}>UTEPs</h1>
            </div>
          </div>

          <div className="flex items-center gap-4">
            {/*Notification dropdown*/}
            <DropdownMenu 
              open={notificationDropdownOpen} 
              onOpenChange={(open) => {
                // Only allow closing, not opening via the trigger
                // Opening is controlled by handleBellClick
                if (!open) {
                  setNotificationDropdownOpen(false);
                }
              }}
            >
              <DropdownMenuTrigger asChild>
                <Button 
                  className="cursor-pointer relative" 
                  variant="ghost" 
                  size="icon"
                  onClick={(e) => {
                    e.preventDefault();
                    handleBellClick();
                  }}
                >
                  <Bell className="w-5 h-5" />
                  {unreadCount > 0 && (
                    <Badge 
                      className="absolute -top-1 -right-1 h-5 w-5 flex items-center justify-center p-0 text-xs"
                      variant="destructive"
                    >
                      {unreadCount > 99 ? '99+' : unreadCount}
                    </Badge>
                  )}
                </Button>
              </DropdownMenuTrigger>

            <DropdownMenuContent className="w-[300px] sm:w-[400px] md:w-[500px] px-3" align="end" alignOffset={-40}>
              <DropdownMenuLabel className="px-0 py-2">
                <div className="text-lg font-bold">Thông báo</div>
              </DropdownMenuLabel>

              <Tabs defaultValue="all">
                {/*Notification displaying types*/}
                <TabsList className="bg-transparent p-0 gap-2">
                  {notificationTypes.map((type) => (
                    <TabsTrigger
                    key={type.id}
                    value={type.id}
                    className="cursor-pointer text-black font-semibold rounded-2xl hover:bg-blue-300 hover:text-white data-[state=active]:bg-blue-500 data-[state=active]:text-white p-2"
                  >
                    {type.title}
                  </TabsTrigger>
                  ))}
                </TabsList>

                {/*All notifications*/}
                <TabsContent value={notificationTypes[0].id}>
                  {isLoading ? (
                    <div className="flex items-center justify-center text-gray-500 py-5">
                      <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                      <span className="ml-2">Đang tải...</span>
                    </div>
                  ) : notifications.length === 0 ? (
                    <div className="flex items-center justify-center text-gray-500 py-5">
                      Bạn chưa có thông báo nào.
                    </div>
                  ) : (
                    notifications.map((notification) => (
                      <div
                        key={notification.id}
                        className={`flex flex-col px-3 py-2 mb-1 ${notification.isRead ? "bg-transparent" : "bg-blue-50 rounded border-yellow-300 cursor-pointer"
                          }`}
                        onClick={() => {handleNotificationClick(notification.id)}}
                      >
                        <div className="flex justify-between items-center">
                          <h3 className="text-sm font-semibold text-gray-800">{notification.title}</h3>
                          <span className="text-xs text-gray-500">
                            {notification.createdAt ? new Date(notification.createdAt).toLocaleDateString('vi-VN') : ''} {notification.createdAt ? new Date(notification.createdAt).toLocaleTimeString('vi-VN') : ''}
                          </span>
                        </div>
                        <p className="text-sm text-gray-700 mt-1">{notification.message}</p>
                        <a
                          href='#'
                          className="text-xs text-blue-600 hover:underline mt-2 w-fit"
                        >
                          Xem chi tiết
                        </a>
                      </div>
                    ))
                  )}
                </TabsContent>

                {/*Notifications are unread */}
                <TabsContent value={notificationTypes[1].id}>
                  {isLoading ? (
                    <div className="flex items-center justify-center text-gray-500 py-5">
                      <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                      <span className="ml-2">Đang tải...</span>
                    </div>
                  ) : notifications.filter(notification => !notification.isRead).length === 0 ? (
                    <div className="flex items-center justify-center text-gray-500 py-5">
                      Bạn chưa có thông báo mới nào.
                    </div>
                  ) : (
                    notifications.filter(notification => !notification.isRead).map((notification) => (
                      <div
                        key={notification.id}
                        className={`flex flex-col px-3 py-2 mb-1 ${notification.isRead ? "bg-transparent" : "bg-blue-50 rounded border-yellow-300 cursor-pointer click:bg-transparent"
                          }`}
                        onClick={() => {handleNotificationClick(notification.id)}}
                      >
                        <div className="flex justify-between items-center">
                          <h3 className="text-sm font-semibold text-gray-800">{notification.title}</h3>
                          <span className="text-xs text-gray-500">
                            {notification.createdAt ? new Date(notification.createdAt).toLocaleDateString('vi-VN') : ''} {notification.createdAt ? new Date(notification.createdAt).toLocaleTimeString('vi-VN') : ''}
                          </span>
                        </div>
                        <p className="text-sm text-gray-700 mt-1">{notification.message}</p>
                        <a
                          href='#'
                          className="text-xs text-blue-600 hover:underline mt-2 w-fit"
                        >
                          Xem chi tiết
                        </a>
                      </div>
                    ))
                  )}
                </TabsContent>

                {/*Notifications are grouped by projects*/}
                <TabsContent value={notificationTypes[2].id}>
                  <img src={developing} alt="Developing" />
                </TabsContent>
              </Tabs>

            </DropdownMenuContent>

          </DropdownMenu>

          {/*Profile dropdown*/}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button className="cursor-pointer" variant="outline" size="icon">
                <Menu className="w-4 h-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuLabel>{user?.displayName || "Tài khoản"}</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem
                onSelect={(event) => {
                  event.preventDefault();
                  setPasswordDialogOpen(true);
                }}
              >
                Đổi mật khẩu
              </DropdownMenuItem>
              <DropdownMenuItem
                onSelect={(event) => {
                  event.preventDefault();
                  handleLogout();
                }}
              >
                Đăng xuất
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
      <ChangePasswordDialog
        open={passwordDialogOpen}
        onOpenChange={setPasswordDialogOpen}
      />
    </header>
    </>
  )
}