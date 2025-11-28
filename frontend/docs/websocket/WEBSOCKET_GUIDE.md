# WebSocket Real-Time Notification Implementation Guide

## 🎯 Overview

This project now supports **real-time notifications** using WebSocket (STOMP protocol over SockJS). When combined with Firebase Cloud Messaging (FCM), you have a **hybrid notification system**:

- **WebSocket**: Real-time updates when user is online (tab open)
- **Firebase FCM**: Push notifications when user is offline

---

## 🏗️ Architecture

```
┌─────────────────┐
│   Backend       │
│  Spring Boot    │
│                 │
│  ┌──────────┐   │
│  │WebSocket │   │
│  │  STOMP   │   │
│  └────┬─────┘   │
│       │         │
│  ┌────┴─────┐   │
│  │ Message  │   │
│  │  Broker  │   │
│  └────┬─────┘   │
│       │         │
└───────┼─────────┘
        │
        │ WebSocket Connection
        │ (SockJS + STOMP)
        │
┌───────┼─────────┐
│       │         │
│  ┌────┴─────┐   │
│  │  React   │   │
│  │  Client  │   │
│  └──────────┘   │
│   Frontend      │
└─────────────────┘
```

---

## 🚀 Setup Instructions

### Backend (Spring Boot)

#### 1. Dependencies Already Added ✅

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

#### 2. Configuration Created ✅

- `WebSocketConfig.java` - WebSocket configuration
- `WebSocketNotificationService.java` - Service for sending WebSocket messages
- `NotificationServiceImpl.java` - Updated to send WebSocket notifications

#### 3. Endpoints Available

- **WebSocket Endpoint**: `ws://localhost:8080/ws`
- **Subscriptions**:
  - `/user/queue/notifications` - User-specific notifications
  - `/user/queue/notification-count` - Unread count updates
  - `/user/queue/notification-updates` - Read/delete updates
  - `/topic/notifications` - Broadcast to all users

---

### Frontend (React + TypeScript)

#### 1. Install Dependencies

```bash
cd frontend
npm install
```

This will install:
- `sockjs-client` - SockJS client for WebSocket fallback
- `@stomp/stompjs` - STOMP protocol over WebSocket

#### 2. Environment Variables

Add to `.env`:

```env
VITE_WS_URL=http://localhost:8080/ws
```

#### 3. Services Created ✅

- `websocket.service.ts` - WebSocket connection manager
- `useWebSocketNotification.ts` - React hook for real-time notifications

---

## 📖 Usage Examples

### Example 1: Basic Usage in React Component

```tsx
import { useWebSocketNotification } from '@/hooks/useWebSocketNotification';

function NotificationBell() {
  const userId = 123; // Get from auth context
  const token = 'your-jwt-token'; // Get from auth context
  
  const { notifications, unreadCount, isConnected } = useWebSocketNotification(userId, token);

  return (
    <div>
      <div className="notification-bell">
        🔔
        {unreadCount > 0 && (
          <span className="badge">{unreadCount}</span>
        )}
      </div>
      
      <div className="connection-status">
        {isConnected ? '🟢 Connected' : '🔴 Disconnected'}
      </div>
      
      <div className="notifications-list">
        {notifications.map(notification => (
          <div key={notification.id}>
            <h4>{notification.title}</h4>
            <p>{notification.message}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
```

### Example 2: Notification List Page with Auto-Refresh

```tsx
import { useWebSocketNotification } from '@/hooks/useWebSocketNotification';
import { useEffect, useState } from 'react';
import { getNotifications } from '@/services/notification.api';

function NotificationListPage() {
  const userId = useAuth().user?.id;
  const token = useAuth().token;
  
  const [allNotifications, setAllNotifications] = useState([]);
  const { notifications: newNotifications, unreadCount } = useWebSocketNotification(userId, token);

  // Load initial notifications
  useEffect(() => {
    loadNotifications();
  }, []);

  // Auto-refresh when new WebSocket notifications arrive
  useEffect(() => {
    if (newNotifications.length > 0) {
      // Prepend new notifications to list
      setAllNotifications(prev => [
        ...newNotifications.filter(n => !prev.find(p => p.id === n.id)),
        ...prev
      ]);
    }
  }, [newNotifications]);

  const loadNotifications = async () => {
    const data = await getNotifications();
    setAllNotifications(data);
  };

  return (
    <div>
      <h1>Notifications ({unreadCount} unread)</h1>
      
      {allNotifications.map(notification => (
        <div key={notification.id} className={notification.isRead ? 'read' : 'unread'}>
          <h3>{notification.title}</h3>
          <p>{notification.message}</p>
          <span>{notification.timestamp}</span>
        </div>
      ))}
    </div>
  );
}
```

### Example 3: Backend - Send Notification with WebSocket

```java
@Service
public class TaskService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private WebSocketNotificationService webSocketNotificationService;
    
    public void assignTask(Long taskId, Long userId) {
        // ... assign task logic
        
        // Create notification in database
        Notification notification = Notification.builder()
            .user(user)
            .title("New Task Assigned")
            .message("You have been assigned to task: " + task.getTitle())
            .type(ENotificationType.TASK_ASSIGNMENT)
            .referenceId(taskId)
            .referenceType("TASK")
            .isRead(false)
            .build();
        
        notification = notificationRepository.save(notification);
        
        // Send real-time WebSocket notification
        WebSocketNotificationMessage wsMessage = webSocketNotificationService
            .convertToWebSocketMessage(notification, "NEW_NOTIFICATION");
        
        webSocketNotificationService.sendNotificationToUser(userId, wsMessage);
        
        // Update unread count
        Long unreadCount = notificationRepository.countByUserAndIsRead(user, false);
        webSocketNotificationService.sendNotificationCount(userId, unreadCount);
    }
}
```

---

## 🔄 Message Flow

### 1. New Notification Flow

```
Backend                          WebSocket                    Frontend
   |                                 |                            |
   | 1. Create notification          |                            |
   |---------------------------------|                            |
   |                                 |                            |
   | 2. Send WebSocket message       |                            |
   |-------------------------------->|                            |
   |                                 |                            |
   |                                 | 3. Route to user queue     |
   |                                 |--------------------------->|
   |                                 |                            |
   |                                 |                    4. React hook receives
   |                                 |                    5. Update UI
   |                                 |                    6. Show browser notification
```

### 2. Mark as Read Flow

```
Frontend                         Backend                     WebSocket
   |                                 |                            |
   | 1. User clicks notification     |                            |
   |                                 |                            |
   | 2. Call API /mark-as-read       |                            |
   |-------------------------------->|                            |
   |                                 |                            |
   |                                 | 3. Update database         |
   |                                 |                            |
   |                                 | 4. Send WebSocket update   |
   |                                 |--------------------------->|
   |                                 |                            |
   |                      5. Receive update (all tabs)            |
   |<------------------------------------------------------------|
   |                                 |                            |
   | 6. Update UI in all open tabs   |                            |
```

---

## 🎨 UI Integration Patterns

### Pattern 1: Global Notification Manager

```tsx
// App.tsx
function App() {
  const { user, token } = useAuth();
  
  return (
    <>
      {user && <WebSocketNotificationManager userId={user.id} token={token} />}
      <Routes>
        {/* ... your routes */}
      </Routes>
    </>
  );
}

// WebSocketNotificationManager.tsx
function WebSocketNotificationManager({ userId, token }) {
  const { notifications, unreadCount } = useWebSocketNotification(userId, token);
  
  // Global notification display logic
  useEffect(() => {
    // Show toast notifications, update global state, etc.
  }, [notifications]);
  
  return null; // This is a headless component
}
```

### Pattern 2: Notification Context

```tsx
// NotificationContext.tsx
const NotificationContext = createContext({});

export function NotificationProvider({ children }) {
  const { user, token } = useAuth();
  const wsNotifications = useWebSocketNotification(user?.id, token);
  
  return (
    <NotificationContext.Provider value={wsNotifications}>
      {children}
    </NotificationContext.Provider>
  );
}

// Use in any component
function SomeComponent() {
  const { notifications, unreadCount } = useContext(NotificationContext);
  // ...
}
```

---

## 🧪 Testing

### 1. Test WebSocket Connection

```bash
# Open browser console
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected:', frame);
    
    stompClient.subscribe('/user/queue/notifications', function(message) {
        console.log('Received:', JSON.parse(message.body));
    });
});
```

### 2. Test Notification Sending (Backend)

```bash
# POST /api/v1/device-tokens/test-notification
# This will send both FCM and WebSocket notifications
```

### 3. Test in Multiple Tabs

1. Open 2 tabs with your app
2. Login as the same user
3. Mark notification as read in tab 1
4. See it update in tab 2 automatically

---

## 🐛 Troubleshooting

### Issue: WebSocket not connecting

**Solution:**
1. Check backend is running on port 8080
2. Verify CORS settings in `WebSocketConfig.java`
3. Check browser console for errors
4. Try SockJS fallback endpoint: `http://localhost:8080/ws/info`

### Issue: Notifications not received

**Solution:**
1. Check user is logged in (userId must be valid)
2. Verify JWT token is passed to WebSocket
3. Check backend logs for WebSocket errors
4. Confirm subscription to correct queue: `/user/queue/notifications`

### Issue: Connection drops frequently

**Solution:**
1. Increase heartbeat intervals in `websocket.service.ts`
2. Check network stability
3. Enable auto-reconnect (already implemented)

---

## 📊 Performance Considerations

### Backend

- ✅ **Simple in-memory broker**: Good for < 1000 concurrent users
- 🔄 **External broker (RabbitMQ/Redis)**: For > 1000 concurrent users
- 📈 **Scaling**: Use session affinity or external message broker

### Frontend

- ✅ **Single WebSocket connection** per user
- ✅ **Auto-reconnect** with exponential backoff
- ✅ **Message batching** for multiple updates

---

## 🔐 Security

### Authentication

- WebSocket connections can be authenticated using JWT
- Token passed in `connectHeaders`
- Backend validates token before accepting connection

### Authorization

- User can only subscribe to their own queue: `/user/{userId}/queue/*`
- Spring Security integration for endpoint protection

---

## 🚀 Next Steps

1. **Install dependencies**: `npm install` in frontend
2. **Start backend**: Spring Boot application
3. **Start frontend**: `npm run dev`
4. **Test**: Open notification page and create a test notification

---

## 📚 Additional Resources

- [Spring WebSocket Docs](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol](https://stomp.github.io/)
- [SockJS](https://github.com/sockjs/sockjs-client)

---

**Happy Real-Time Coding! 🎉**
