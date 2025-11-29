import { useEffect, useState } from 'react';
import {
  requestNotificationPermission,
  onMessageListener,
  isNotificationSupported,
  getNotificationPermission,
} from '@/services/firebase.service';
import { registerDeviceToken } from '@/services/notification.api';

interface NotificationPayload {
  notification?: {
    title: string;
    body: string;
    image?: string;
  };
  data?: {
    [key: string]: string;
  };
}

export const useNotification = () => {
  const [fcmToken, setFcmToken] = useState<string | null>(null);
  const [notification, setNotification] = useState<NotificationPayload | null>(null);
  const [isSupported, setIsSupported] = useState(false);
  const [permission, setPermission] = useState<NotificationPermission>('default');

  useEffect(() => {
    // Check if notifications are supported
    setIsSupported(isNotificationSupported());
    setPermission(getNotificationPermission());
  }, []);

  /**
   * Request permission and get FCM token
   */
  const requestPermission = async () => {
    try {
      const token = await requestNotificationPermission();
      
      if (token) {
        setFcmToken(token);
        setPermission('granted');
        console.log('FCM Token obtained:', token);
        // Register token to backend
        await registerDeviceToken(token, 'WEB');
        
        // Store token in localStorage for later use
        localStorage.setItem('fcmToken', token);
        
        return token;
      }
      return null;
    } catch (error) {
      console.error('Error requesting notification permission:', error);
      return null;
    }
  };

  /**
   * Listen for foreground messages
   */
  useEffect(() => {
    if (permission === 'granted') {
      onMessageListener()
        .then((payload) => {
          console.log('Notification received:', payload);
          setNotification(payload);
          
          // Show browser notification với âm thanh
          if (payload.notification) {
            new Notification(payload.notification.title || 'New Notification', {
              body: payload.notification.body,
              icon: payload.notification.image || '/vite.svg',
              badge: '/vite.svg',
              tag: payload.data?.type || 'default',
              requireInteraction: false,
              silent: false, // KEY: Đảm bảo có âm thanh!
              data: payload.data,
            } as NotificationOptions);
          }
        })
        .catch((err) => console.error('Error listening for messages:', err));
    }
  }, [permission]);

  /**
   * Clear notification
   */
  const clearNotification = () => {
    setNotification(null);
  };

  return {
    fcmToken,
    notification,
    isSupported,
    permission,
    requestPermission,
    clearNotification,
  };
};
