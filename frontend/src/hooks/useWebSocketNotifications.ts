import { useEffect, useState, useCallback } from 'react';
import { webSocketService, WebSocketNotification } from '@/services/websocket.service';
import { useAuth } from '@/contexts/AuthContext';
import { useToast } from '@/components/ui/toast';
import { 
  getMyNotifications, 
  countUnreadNotifications, 
  markNotificationAsRead 
} from '@/services/notification.api';

export const useWebSocketNotifications = () => {
  const [notifications, setNotifications] = useState<WebSocketNotification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isConnected, setIsConnected] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useAuth();
  const { addToast } = useToast();

  // Handle incoming WebSocket notifications
  const handleNotification = useCallback((notification: WebSocketNotification) => {
    console.log('Received WebSocket notification:', notification);

    // Add to notifications list
    setNotifications((prev) => [notification, ...prev]);

    // Show toast for new notifications
    if (notification.action === 'NEW_NOTIFICATION') {
      addToast({
        title: notification.title,
        description: notification.message,
        variant: 'default',
      });

      // Play notification sound (optional)
      try {
        const audio = new Audio('/notification-sound.mp3');
        audio.volume = 0.5;
        audio.play().catch((e) => console.log('Could not play sound:', e));
      } catch (e) {
        console.log('Audio not supported or error:', e);
      }
    }
  }, [addToast]);

  // Handle notification count updates
  const handleNotificationCount = useCallback((count: number) => {
    console.log('Unread count updated:', count);
    setUnreadCount(count);
  }, []);

  // Handle connection status changes
  const handleConnectionChange = useCallback((connected: boolean) => {
    console.log('WebSocket connection status:', connected);
    setIsConnected(connected);
  }, []);

  // Fetch initial notifications from API
  useEffect(() => {
    const fetchNotifications = async () => {
      if (user && user.id) {
        setIsLoading(true);
        try {
          // Fetch notifications from backend
          const response = await getMyNotifications(user.id);
          if (response.data) {
            setNotifications(response.data);
          }

          // Fetch unread count
          const countResponse = await countUnreadNotifications(user.id);
          if (countResponse.data !== undefined) {
            setUnreadCount(countResponse.data);
          }
        } catch (error) {
          console.error('Error fetching notifications:', error);
        } finally {
          setIsLoading(false);
        }
      }
    };

    fetchNotifications();
  }, [user]);

  // Connect to WebSocket when user is authenticated
  useEffect(() => {
    if (user && user.id) {
      const token = localStorage.getItem('accessToken');
      
      // Subscribe to callbacks
      webSocketService.onNotification(handleNotification);
      webSocketService.onNotificationCount(handleNotificationCount);
      webSocketService.onConnectionChange(handleConnectionChange);

      // Connect
      webSocketService.connect(user.id, token || undefined);

      // Cleanup on unmount
      return () => {
        webSocketService.disconnect();
      };
    }
  }, [user, handleNotification, handleNotificationCount, handleConnectionChange]);

  // Mark notification as read
  const markAsRead = useCallback(async (notificationId: number) => {
    try {
      // Call API to mark as read in backend
      await markNotificationAsRead(notificationId);
      
      // Update local state
      setNotifications((prev) =>
        prev.map((n) =>
          n.id === notificationId ? { ...n, isRead: true } : n
        )
      );
      
      // Update unread count
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  }, []);

  // Clear all notifications
  const clearNotifications = useCallback(() => {
    setNotifications([]);
  }, []);

  return {
    notifications,
    unreadCount,
    isConnected,
    isLoading,
    markAsRead,
    clearNotifications,
  };
};
