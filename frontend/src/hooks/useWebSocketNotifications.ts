import { useEffect, useState, useCallback } from 'react';
import { webSocketService, WebSocketNotification } from '@/services/websocket.service';
import { useAuth } from '@/contexts/AuthContext';
import { useToast } from '@/components/ui/toast';
import { 
  getMyNotifications, 
  countUnreadNotifications, 
  markNotificationAsRead 
} from '@/services/notification.api';
import { authService } from '@/services/auth.service';
import { isTokenExpired } from '@/utils/jwt.utils';

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

    // Add to notifications list and sort by createdAt (newest first)
    setNotifications((prev) => {
      const updated = [notification, ...prev];
      return updated.sort((a, b) => 
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
    });

    // Show toast for new notifications
    if (notification.action === 'NEW_NOTIFICATION') {
      addToast({
        title: notification.title,
        description: notification.message,
        variant: 'default',
      });

      // Play notification sound from online URL
      try {
        const audio = new Audio('https://cdn.pixabay.com/audio/2021/08/04/audio_0625c1539c.mp3');
        audio.volume = 0.3;
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
            // Sort notifications by createdAt (newest first)
            const sortedNotifications = response.data.sort((a: WebSocketNotification, b: WebSocketNotification) => 
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
            );
            setNotifications(sortedNotifications);
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
    if (!user?.id) return;

    let unsubNotification: (() => void) | undefined;
    let unsubCount: (() => void) | undefined;
    let unsubConnection: (() => void) | undefined;

    const connectWebSocket = async () => {
      let token = localStorage.getItem('accessToken');
      
      // Check if token is expired or about to expire
      if (token && isTokenExpired(token)) {
        console.log('🔄 Access token expired, refreshing before WebSocket connection...');
        
        try {
          // Refresh token before connecting
          const response = await authService.refreshToken();
          if (response.data?.accessToken) {
            token = response.data.accessToken;
            localStorage.setItem('accessToken', token);
            console.log('✅ Token refreshed successfully for WebSocket connection');
          }
        } catch (error) {
          console.error('❌ Failed to refresh token for WebSocket:', error);
          // Continue with old token, backend will handle gracefully
        }
      }
      
      // Subscribe to callbacks BEFORE connecting
      unsubNotification = webSocketService.onNotification(handleNotification);
      unsubCount = webSocketService.onNotificationCount(handleNotificationCount);
      unsubConnection = webSocketService.onConnectionChange(handleConnectionChange);

      // Connect with fresh token
      webSocketService.connect(user.id, token || undefined);
    };

    connectWebSocket();
    
    // Cleanup function
    return () => {
      console.log('🧹 Cleaning up WebSocket connection...');
      unsubNotification?.();
      unsubCount?.();
      unsubConnection?.();
      webSocketService.disconnect();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]); // Only re-run when user ID changes

  // Mark notification as read
  const markAsRead = useCallback(async (notificationId: number) => {
    try {
      // Call API to mark as read in backend
      await markNotificationAsRead(notificationId);
      
      // Update local state and maintain sort order
      setNotifications((prev) => {
        const updated = prev.map((n) =>
          n.id === notificationId ? { ...n, isRead: true } : n
        );
        // Keep sorted by createdAt (newest first)
        return updated.sort((a, b) => 
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
      });
      
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
