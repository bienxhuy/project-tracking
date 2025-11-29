import axios from 'axios';

const apiClient = axios.create({
  baseURL: "http://localhost:9090",
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests if available
apiClient.interceptors.request.use((config: any) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * Register device token to backend
 */
export const registerDeviceToken = async (
  fcmToken: string,
  deviceType: 'WEB' | 'ANDROID' | 'IOS' = 'WEB',
  deviceInfo?: string
) => {
  try {
    const response = await apiClient.post('/api/v1/device-tokens/register', {
      fcmToken,
      deviceType,
      deviceInfo: deviceInfo || navigator.userAgent,
    });
    return response.data;
  } catch (error) {
    console.error('Error registering device token:', error);
    throw error;
  }
};

/**
 * Delete device token from backend
 */
export const deleteDeviceToken = async (fcmToken: string) => {
  try {
    const response = await apiClient.delete(`/api/v1/device-tokens/${fcmToken}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting device token:', error);
    throw error;
  }
};

/**
 * Get user's device tokens
 */
export const getMyDeviceTokens = async () => {
  try {
    const response = await apiClient.get('/api/v1/device-tokens/my-tokens');
    return response.data;
  } catch (error) {
    console.error('Error getting device tokens:', error);
    throw error;
  }
};

/**
 * Debug: Get detailed token information
 */
export const debugDeviceTokens = async () => {
  try {
    const response = await apiClient.get('/api/v1/device-tokens/debug');
    return response.data;
  } catch (error) {
    console.error('Error debugging device tokens:', error);
    throw error;
  }
};

/**
 * Send test notification
 */
export const sendTestNotification = async () => {
  try {
    const response = await apiClient.post('/api/v1/device-tokens/test-notification');
    return response.data;
  } catch (error) {
    console.error('Error sending test notification:', error);
    throw error;
  }
};

// ============================================
// Notification Management APIs
// ============================================

/**
 * Get notification by ID
 */
export const getNotificationById = async (id: number) => {
  try {
    const response = await apiClient.get(`/api/v1/notifications/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error getting notification:', error);
    throw error;
  }
};

/**
 * Get all notifications for current user
 * Note: You need to pass the current user's ID
 */
export const getMyNotifications = async (userId: number) => {
  try {
    const response = await apiClient.get(`/api/v1/notifications/user/${userId}`);
    return response.data;
  } catch (error) {
    console.error('Error getting my notifications:', error);
    throw error;
  }
};

/**
 * Get notifications by user ID
 */
export const getNotificationsByUser = async (userId: number) => {
  try {
    const response = await apiClient.get(`/api/v1/notifications/user/${userId}`);
    return response.data;
  } catch (error) {
    console.error('Error getting user notifications:', error);
    throw error;
  }
};

/**
 * Get unread notifications for user
 */
export const getUnreadNotifications = async (userId: number) => {
  try {
    const response = await apiClient.get(`/api/v1/notifications/user/${userId}/unread`);
    return response.data;
  } catch (error) {
    console.error('Error getting unread notifications:', error);
    throw error;
  }
};

/**
 * Count unread notifications
 */
export const countUnreadNotifications = async (userId: number) => {
  try {
    const response = await apiClient.get(`/api/v1/notifications/user/${userId}/unread/count`);
    return response.data;
  } catch (error) {
    console.error('Error counting unread notifications:', error);
    throw error;
  }
};

/**
 * Mark notification as read
 */
export const markNotificationAsRead = async (id: number) => {
  try {
    const response = await apiClient.patch(`/api/v1/notifications/${id}/read`);
    return response.data;
  } catch (error) {
    console.error('Error marking notification as read:', error);
    throw error;
  }
};

/**
 * Mark all notifications as read for user
 */
export const markAllNotificationsAsRead = async (userId: number) => {
  try {
    const response = await apiClient.patch(`/api/v1/notifications/user/${userId}/read-all`);
    return response.data;
  } catch (error) {
    console.error('Error marking all notifications as read:', error);
    throw error;
  }
};

/**
 * Delete notification
 */
export const deleteNotification = async (id: number) => {
  try {
    const response = await apiClient.delete(`/api/v1/notifications/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting notification:', error);
    throw error;
  }
};

export default apiClient;
