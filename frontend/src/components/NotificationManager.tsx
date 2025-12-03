import { useEffect } from 'react';
import { useNotification } from '@/hooks/useNotification';
import { Bell, BellOff, X } from 'lucide-react';

export const NotificationManager = () => {
  const {
    fcmToken,
    notification,
    isSupported,
    permission,
    requestPermission,
    clearNotification,
  } = useNotification();

  useEffect(() => {
    // Auto-request permission on mount if not yet determined
    if (isSupported && permission === 'default') {
      // You might want to show a custom UI first before requesting
      // requestPermission();
    }
  }, [isSupported, permission]);

  // Auto-hide notification after 5 seconds
  useEffect(() => {
    if (notification) {
      const timer = setTimeout(() => {
        clearNotification();
      }, 5000); // 5 seconds

      return () => clearTimeout(timer);
    }
  }, [notification, clearNotification]);

  if (!isSupported) {
    return (
      <div className="fixed bottom-4 right-4 bg-yellow-100 border border-yellow-400 text-yellow-700 px-4 py-3 rounded">
        <p className="text-sm">Trình duyệt của bạn không hỗ trợ thông báo</p>
      </div>
    );
  }

  return (
    <>
      {/* Notification Permission Button */}
      {permission !== 'granted' && (
        <button
          onClick={requestPermission}
          className="fixed bottom-4 right-4 bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-lg flex items-center gap-2 transition-colors"
        >
          <Bell size={20} />
          Bật thông báo
        </button>
      )}

      {/* Notification Status Indicator */}
      {permission === 'granted' && (
        <div className="fixed top-4 right-4 bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs flex items-center gap-1">
          <Bell size={14} />
          Thông báo đã bật
        </div>
      )}

      {permission === 'denied' && (
        <div className="fixed top-4 right-4 bg-red-100 text-red-700 px-3 py-1 rounded-full text-xs flex items-center gap-1">
          <BellOff size={14} />
          Thông báo bị chặn
        </div>
      )}

      {/* Foreground Notification Toast */}
      {notification && (
        <div className="fixed top-16 right-4 bg-white shadow-lg rounded-lg p-4 max-w-sm border-l-4 border-blue-500 animate-slide-in">
          <div className="flex justify-between items-start gap-2">
            <div className="flex-1">
              <h4 className="font-semibold text-gray-900">
                {notification.notification?.title}
              </h4>
              <p className="text-sm text-gray-600 mt-1">
                {notification.notification?.body}
              </p>
              {notification.data && (
                <div className="mt-2 text-xs text-gray-500 space-y-1">
                  {notification.data.type && (
                    <div>
                      <strong>Loại thông báo:</strong> {notification.data.type.replace(/_/g, ' ')}
                    </div>
                  )}
                  {notification.data.timestamp && (
                    <div>
                      <strong>Thời gian:</strong> {new Date(notification.data.timestamp).toLocaleString('vi-VN', {
                        hour: '2-digit',
                        minute: '2-digit',
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric'
                      })}
                    </div>
                  )}
                </div>
              )}
            </div>
            <button
              onClick={clearNotification}
              className="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X size={18} />
            </button>
          </div>
        </div>
      )}

      {/* Debug Info (Remove in production) */}
      {fcmToken && process.env.NODE_ENV === 'development' && (
        <div className="fixed bottom-4 left-4 bg-gray-100 text-gray-700 px-3 py-2 rounded text-xs max-w-sm break-all">
          <strong>FCM Token:</strong>
          <div className="mt-1 font-mono text-xs">{fcmToken.substring(0, 50)}...</div>
        </div>
      )}
    </>
  );
};
