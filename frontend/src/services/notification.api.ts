import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
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

export default apiClient;
