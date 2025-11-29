import { useEffect, useState, useCallback, useRef } from 'react';
import { webSocketService, WebSocketNotification } from '@/services/websocket.service';

interface UseWebSocketNotificationReturn {
  notifications: WebSocketNotification[];
  unreadCount: number;
  isConnected: boolean;
  addNotification: (notification: WebSocketNotification) => void;
  removeNotification: (id: number) => void;
  markAsRead: (id: number) => void;
  clearAll: () => void;
}

/**
 * React hook for WebSocket real-time notifications
 * 
 * Usage:
 * ```tsx
 * const { notifications, unreadCount, isConnected } = useWebSocketNotification(userId, token);
 * ```
 */
export const useWebSocketNotification = (
  userId?: number,
  token?: string
): UseWebSocketNotificationReturn => {
  const [notifications, setNotifications] = useState<WebSocketNotification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isConnected, setIsConnected] = useState(false);
  const hasConnected = useRef(false);

  /**
   * Add new notification to list
   */
  const addNotification = useCallback((notification: WebSocketNotification) => {
    setNotifications(prev => {
      // Avoid duplicates
      if (prev.find(n => n.id === notification.id)) {
        return prev;
      }
      return [notification, ...prev];
    });
  }, []);

  /**
   * Remove notification from list
   */
  const removeNotification = useCallback((id: number) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  }, []);

  /**
   * Mark notification as read in local state
   */
  const markAsRead = useCallback((id: number) => {
    setNotifications(prev =>
      prev.map(n => (n.id === id ? { ...n, isRead: true } : n))
    );
  }, []);

  /**
   * Clear all notifications
   */
  const clearAll = useCallback(() => {
    setNotifications([]);
    setUnreadCount(0);
  }, []);

  /**
   * Handle incoming WebSocket notifications
   */
  useEffect(() => {
    if (!userId || hasConnected.current) return;

    // Connect to WebSocket
    webSocketService.connect(userId, token);
    hasConnected.current = true;

    // Listen for new notifications
    const unsubscribeNotification = webSocketService.onNotification((notification) => {
      console.log('Hook received notification:', notification);
      
      if (notification.action === 'NEW_NOTIFICATION') {
        addNotification(notification);
        
        // Show browser notification if permission granted
        if (Notification.permission === 'granted') {
          new Notification(notification.title, {
            body: notification.message,
            icon: '/vite.svg',
            tag: `notification-${notification.id}`,
          });
        }
      } else if (notification.action === 'NOTIFICATION_READ') {
        markAsRead(notification.id);
      } else if (notification.action === 'NOTIFICATION_DELETED') {
        removeNotification(notification.id);
      }
    });

    // Listen for notification count updates
    const unsubscribeCount = webSocketService.onNotificationCount((count) => {
      console.log('Hook received count update:', count);
      setUnreadCount(count);
    });

    // Listen for connection status
    const unsubscribeConnection = webSocketService.onConnectionChange((connected) => {
      console.log('WebSocket connection status:', connected);
      setIsConnected(connected);
    });

    // Cleanup on unmount
    return () => {
      unsubscribeNotification();
      unsubscribeCount();
      unsubscribeConnection();
      webSocketService.disconnect();
      hasConnected.current = false;
    };
  }, [userId, token, addNotification, markAsRead, removeNotification]);

  return {
    notifications,
    unreadCount,
    isConnected,
    addNotification,
    removeNotification,
    markAsRead,
    clearAll,
  };
};
