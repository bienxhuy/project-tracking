import { useEffect, useState } from 'react';
import { useWebSocketNotification } from '@/hooks/useWebSocketNotification';

/**
 * Component để test WebSocket connection và notifications
 * Sử dụng tạm để verify WebSocket hoạt động
 */
export const WebSocketTest = () => {
  const [userId, setUserId] = useState<number>(123); // Thay đổi userId của bạn
  const [token, setToken] = useState<string>(''); // JWT token của bạn

  const {
    notifications,
    unreadCount,
    isConnected,
    addNotification,
    removeNotification,
    markAsRead,
    clearAll,
  } = useWebSocketNotification(userId, token);

  // Request browser notification permission
  useEffect(() => {
    if (Notification.permission === 'default') {
      Notification.requestPermission();
    }
  }, []);

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>WebSocket Test Dashboard</h1>

      {/* Connection Status */}
      <div style={{
        padding: '10px',
        marginBottom: '20px',
        backgroundColor: isConnected ? '#d4edda' : '#f8d7da',
        border: `1px solid ${isConnected ? '#c3e6cb' : '#f5c6cb'}`,
        borderRadius: '5px',
      }}>
        <h2>Connection Status</h2>
        <p>
          Status: {' '}
          <strong style={{ color: isConnected ? 'green' : 'red' }}>
            {isConnected ? '🟢 CONNECTED' : '🔴 DISCONNECTED'}
          </strong>
        </p>
        <p>User ID: <strong>{userId}</strong></p>
      </div>

      {/* Notification Count */}
      <div style={{
        padding: '10px',
        marginBottom: '20px',
        backgroundColor: '#d1ecf1',
        border: '1px solid #bee5eb',
        borderRadius: '5px',
      }}>
        <h2>Unread Count</h2>
        <p style={{ fontSize: '24px', margin: '10px 0' }}>
          <strong>{unreadCount}</strong> unread notifications
        </p>
      </div>

      {/* Actions */}
      <div style={{
        padding: '10px',
        marginBottom: '20px',
        backgroundColor: '#fff3cd',
        border: '1px solid #ffeeba',
        borderRadius: '5px',
      }}>
        <h2>Actions</h2>
        <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          <button
            onClick={clearAll}
            style={{
              padding: '10px 20px',
              backgroundColor: '#dc3545',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer',
            }}
          >
            Clear All Notifications
          </button>

          <button
            onClick={() => {
              // Test manual add
              addNotification({
                id: Date.now(),
                title: 'Test Notification',
                message: 'This is a test notification added manually',
                type: 'INFO',
                timestamp: new Date().toISOString(),
                isRead: false,
                action: 'NEW_NOTIFICATION',
              });
            }}
            style={{
              padding: '10px 20px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer',
            }}
          >
            Add Test Notification (Manual)
          </button>
        </div>
      </div>

      {/* Notifications List */}
      <div style={{
        padding: '10px',
        backgroundColor: '#f8f9fa',
        border: '1px solid #dee2e6',
        borderRadius: '5px',
      }}>
        <h2>Notifications ({notifications.length})</h2>

        {notifications.length === 0 ? (
          <p style={{ color: '#6c757d', fontStyle: 'italic' }}>
            No notifications yet. Waiting for WebSocket messages...
          </p>
        ) : (
          <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
            {notifications.map((notification) => (
              <div
                key={notification.id}
                style={{
                  padding: '15px',
                  marginBottom: '10px',
                  backgroundColor: notification.isRead ? '#e9ecef' : 'white',
                  border: '1px solid #dee2e6',
                  borderRadius: '5px',
                  opacity: notification.isRead ? 0.7 : 1,
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                  <div style={{ flex: 1 }}>
                    <h3 style={{ margin: '0 0 5px 0', fontSize: '16px' }}>
                      {notification.title}
                      {!notification.isRead && (
                        <span style={{
                          marginLeft: '10px',
                          padding: '2px 8px',
                          backgroundColor: '#007bff',
                          color: 'white',
                          fontSize: '12px',
                          borderRadius: '3px',
                        }}>
                          NEW
                        </span>
                      )}
                    </h3>
                    <p style={{ margin: '5px 0', color: '#6c757d' }}>
                      {notification.message}
                    </p>
                    <div style={{ fontSize: '12px', color: '#6c757d', marginTop: '5px' }}>
                      <span>ID: {notification.id}</span>
                      {' | '}
                      <span>Type: {notification.type}</span>
                      {' | '}
                      <span>Action: {notification.action}</span>
                      {' | '}
                      <span>Time: {new Date(notification.timestamp).toLocaleString()}</span>
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '5px', marginLeft: '10px' }}>
                    {!notification.isRead && (
                      <button
                        onClick={() => markAsRead(notification.id)}
                        style={{
                          padding: '5px 10px',
                          backgroundColor: '#28a745',
                          color: 'white',
                          border: 'none',
                          borderRadius: '3px',
                          cursor: 'pointer',
                          fontSize: '12px',
                        }}
                      >
                        Mark Read
                      </button>
                    )}
                    <button
                      onClick={() => removeNotification(notification.id)}
                      style={{
                        padding: '5px 10px',
                        backgroundColor: '#dc3545',
                        color: 'white',
                        border: 'none',
                        borderRadius: '3px',
                        cursor: 'pointer',
                        fontSize: '12px',
                      }}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Console Logs */}
      <div style={{
        marginTop: '20px',
        padding: '10px',
        backgroundColor: '#343a40',
        color: '#f8f9fa',
        borderRadius: '5px',
        fontFamily: 'monospace',
        fontSize: '12px',
      }}>
        <h3 style={{ color: '#f8f9fa' }}>💡 Tips:</h3>
        <ul>
          <li>Check browser console (F12) for WebSocket debug logs</li>
          <li>Check Network tab → WS to see WebSocket connection</li>
          <li>Messages should appear here when backend sends notifications</li>
          <li>Browser notifications require permission (click Allow when prompted)</li>
        </ul>
      </div>
    </div>
  );
};
