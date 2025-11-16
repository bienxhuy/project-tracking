import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage, Messaging } from 'firebase/messaging';
import { firebaseConfig, vapidKey } from '@/config/firebase.config';

let messaging: Messaging | null = null;

/**
 * Initialize Firebase App and Messaging
 */
export const initializeFirebase = () => {
  try {
    const app = initializeApp(firebaseConfig);
    messaging = getMessaging(app);
    console.log('Firebase initialized successfully');
    return messaging;
  } catch (error) {
    console.error('Error initializing Firebase:', error);
    return null;
  }
};

/**
 * Request notification permission and get FCM token
 */
export const requestNotificationPermission = async (): Promise<string | null> => {
  try {
    // Check if browser supports notifications
    if (!('Notification' in window)) {
      console.warn('This browser does not support notifications');
      return null;
    }

    // Request permission
    const permission = await Notification.requestPermission();
    
    if (permission !== 'granted') {
      console.warn('Notification permission denied');
      return null;
    }

    // Initialize Firebase if not already done
    if (!messaging) {
      initializeFirebase();
    }

    if (!messaging) {
      console.error('Firebase Messaging not initialized');
      return null;
    }

    // Get FCM token
    const token = await getToken(messaging, { vapidKey });
    console.log('FCM registration token:', token);
    if (token) {
      console.log('FCM Token:', token);
      return token;
    } else {
      console.warn('No registration token available');
      return null;
    }
  } catch (error) {
    console.error('Error getting FCM token:', error);
    return null;
  }
};

/**
 * Listen for foreground messages
 */
export const onMessageListener = (): Promise<any> => {
  return new Promise((resolve) => {
    if (!messaging) {
      initializeFirebase();
    }

    if (!messaging) {
      console.error('Firebase Messaging not initialized');
      return;
    }

    onMessage(messaging, (payload) => {
      console.log('Message received in foreground:', payload);
      resolve(payload);
    });
  });
};

/**
 * Check if notifications are supported
 */
export const isNotificationSupported = (): boolean => {
  return 'Notification' in window && 'serviceWorker' in navigator;
};

/**
 * Get current notification permission status
 */
export const getNotificationPermission = (): NotificationPermission => {
  return Notification.permission;
};
