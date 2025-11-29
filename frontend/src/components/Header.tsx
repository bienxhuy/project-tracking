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

import { Bell, Menu } from "lucide-react"
import logo from "@/assets/logo.png"
import developing from "@/assets/developing.jpg"
import { useEffect, useState } from "react";
import { useToast } from "@/components/ui/toast";
import { useAuth } from "@/contexts/AuthContext";

import { fetchNotifications } from "@/services/notification.service";
import { Notification } from "@/types/notification.type";
import { ChangePasswordDialog } from "@/components/auth/ChangePasswordDialog";
import { useNavigate } from "react-router-dom";

// Notification displaying types
const notificationTypes = [
  { id: "all", title: "Tất cả" },
  { id: "unread", title: "Chưa đọc" },
  { id: "group", title: "Dự án" },
]

export const Header = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const { logout, user } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch notifications when the component mounts
    const data = fetchNotifications();
    setNotifications(data);
  }, []);

  // Mark notification as read
  const handleNotificationClick = (notification: Notification) => {
    notification.isRead = true;
    setNotifications([...notifications]);
    // TODO: Call API to update notification status in the backend
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
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button className="cursor-pointer" variant="ghost" size="icon">
                <Bell className="w-5 h-5" />
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
                  {notifications.length === 0 ? (
                    <div></div>
                  ) : (
                    notifications.map((notification) => (
                      <div
                        key={notification.id}
                        className={`flex flex-col px-3 py-2 mb-1 ${notification.isRead ? "bg-transparent" : "bg-blue-50 rounded border-yellow-300 cursor-pointer"
                          }`}
                        onClick={() => {handleNotificationClick(notification)}}
                      >
                        <div className="flex justify-between items-center">
                          <h3 className="text-sm font-semibold text-gray-800">{notification.title}</h3>
                          <span className="text-xs text-gray-500">
                            {notification.createdDate.toLocaleDateString()} {notification.createdDate.toLocaleTimeString()}
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
                  {notifications.filter(notification => !notification.isRead).length === 0 ? (
                    <div className="flex items-center justify-center text-gray-500 py-5">
                      Bạn chưa có thông báo mới nào.
                    </div>
                  ) : (
                    notifications.filter(notification => !notification.isRead).map((notification) => (
                      <div
                        key={notification.id}
                        className={`flex flex-col px-3 py-2 mb-1 ${notification.isRead ? "bg-transparent" : "bg-blue-50 rounded border-yellow-300 cursor-pointer click:bg-transparent"
                          }`}
                        onClick={() => {handleNotificationClick(notification)}}
                      >
                        <div className="flex justify-between items-center">
                          <h3 className="text-sm font-semibold text-gray-800">{notification.title}</h3>
                          <span className="text-xs text-gray-500">
                            {notification.createdDate.toLocaleDateString()} {notification.createdDate.toLocaleTimeString()}
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
  )
}