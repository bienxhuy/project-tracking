import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface WebSocketNotification {
  id: number;
  title: string;
  message: string;
  type: string;
  referenceId?: number;
  referenceType?: string;
  triggeredById?: number;
  triggeredByName?: string;
  createdAt: string;  // Changed from timestamp to createdAt to match backend
  isRead: boolean;
  action: string; // 'NEW_NOTIFICATION', 'NOTIFICATION_READ', 'NOTIFICATION_DELETED'
}

type NotificationCallback = (notification: WebSocketNotification) => void;
type NotificationCountCallback = (count: number) => void;
type ConnectionCallback = (connected: boolean) => void;

class WebSocketService {
  private client: Client | null = null;
  private isConnected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  
  private notificationCallbacks: NotificationCallback[] = [];
  private notificationCountCallbacks: NotificationCountCallback[] = [];
  private connectionCallbacks: ConnectionCallback[] = [];

  /**
   * Connect to WebSocket server
   * @param userId User ID for user-specific subscriptions
   * @param token JWT token for authentication (optional)
   */
  connect(userId: number, token?: string) {
    if (this.isConnected) {
      console.log('WebSocket already connected');
      return;
    }

    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:9090/ws';

    console.log('Connecting to WebSocket:', wsUrl);

    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      
      connectHeaders: token ? {
        Authorization: `Bearer ${token}`,
      } : {},

      debug: (str) => {
        if (import.meta.env.DEV) {
          console.log('[WebSocket Debug]', str);
        }
      },

      reconnectDelay: this.reconnectDelay,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,

      onConnect: () => {
        console.log('WebSocket connected successfully');
        this.isConnected = true;
        this.reconnectAttempts = 0;
        this.notifyConnectionCallbacks(true);
        this.subscribeToTopics(userId);
      },

      onStompError: (frame) => {
        console.error('WebSocket STOMP error:', frame);
        this.isConnected = false;
        this.notifyConnectionCallbacks(false);
      },

      onWebSocketError: (event) => {
        console.error('WebSocket error:', event);
        this.isConnected = false;
        this.notifyConnectionCallbacks(false);
      },

      onDisconnect: () => {
        console.log('WebSocket disconnected');
        this.isConnected = false;
        this.notifyConnectionCallbacks(false);
        this.handleReconnect(userId, token);
      },
    });

    this.client.activate();
  }

  /**
   * Subscribe to notification topics
   */
  private subscribeToTopics(userId: number) {
    if (!this.client) return;

    // Subscribe to user-specific notifications
    this.client.subscribe(`/user/queue/notifications`, (message: IMessage) => {
      const notification: WebSocketNotification = JSON.parse(message.body);
      console.log('Received notification:', notification);
      this.notifyNotificationCallbacks(notification);
    });

    // Subscribe to notification count updates
    this.client.subscribe(`/user/queue/notification-count`, (message: IMessage) => {
      const count: number = JSON.parse(message.body);
      console.log('Received notification count:', count);
      this.notifyNotificationCountCallbacks(count);
    });

    // Subscribe to notification updates (read, deleted)
    this.client.subscribe(`/user/queue/notification-updates`, (message: IMessage) => {
      const notification: WebSocketNotification = JSON.parse(message.body);
      console.log('Received notification update:', notification);
      this.notifyNotificationCallbacks(notification);
    });

    console.log(`Subscribed to notification topics for user ${userId}`);
  }

  /**
   * Handle reconnection logic
   */
  private handleReconnect(userId: number, token?: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
      
      setTimeout(() => {
        this.connect(userId, token);
      }, this.reconnectDelay * this.reconnectAttempts);
    } else {
      console.error('Max reconnection attempts reached');
    }
  }

  /**
   * Disconnect from WebSocket
   */
  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.isConnected = false;
      console.log('WebSocket disconnected manually');
    }
  }

  /**
   * Register callback for new notifications
   */
  onNotification(callback: NotificationCallback) {
    this.notificationCallbacks.push(callback);
    
    // Return unsubscribe function
    return () => {
      this.notificationCallbacks = this.notificationCallbacks.filter(cb => cb !== callback);
    };
  }

  /**
   * Register callback for notification count updates
   */
  onNotificationCount(callback: NotificationCountCallback) {
    this.notificationCountCallbacks.push(callback);
    
    return () => {
      this.notificationCountCallbacks = this.notificationCountCallbacks.filter(cb => cb !== callback);
    };
  }

  /**
   * Register callback for connection status changes
   */
  onConnectionChange(callback: ConnectionCallback) {
    this.connectionCallbacks.push(callback);
    
    return () => {
      this.connectionCallbacks = this.connectionCallbacks.filter(cb => cb !== callback);
    };
  }

  /**
   * Notify all notification callbacks
   */
  private notifyNotificationCallbacks(notification: WebSocketNotification) {
    this.notificationCallbacks.forEach(callback => {
      try {
        callback(notification);
      } catch (error) {
        console.error('Error in notification callback:', error);
      }
    });
  }

  /**
   * Notify all notification count callbacks
   */
  private notifyNotificationCountCallbacks(count: number) {
    this.notificationCountCallbacks.forEach(callback => {
      try {
        callback(count);
      } catch (error) {
        console.error('Error in notification count callback:', error);
      }
    });
  }

  /**
   * Notify all connection callbacks
   */
  private notifyConnectionCallbacks(connected: boolean) {
    this.connectionCallbacks.forEach(callback => {
      try {
        callback(connected);
      } catch (error) {
        console.error('Error in connection callback:', error);
      }
    });
  }

  /**
   * Check if WebSocket is connected
   */
  getConnectionStatus(): boolean {
    return this.isConnected;
  }

  /**
   * Send a message to the server (optional - for bidirectional communication)
   */
  send(destination: string, body: any) {
    if (this.client && this.isConnected) {
      this.client.publish({
        destination,
        body: JSON.stringify(body),
      });
    } else {
      console.error('WebSocket is not connected');
    }
  }
}

// Export singleton instance
export const webSocketService = new WebSocketService();
