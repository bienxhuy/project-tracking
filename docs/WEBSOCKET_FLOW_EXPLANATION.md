# 📡 Luồng WebSocket trong Project - Giải thích Chi tiết

## 📋 Mục Lục
1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc Tổng Thể](#kiến-trúc-tổng-thể)
3. [Luồng Backend](#luồng-backend)
4. [Luồng Frontend](#luồng-frontend)
5. [Luồng Hoạt Động End-to-End](#luồng-hoạt-động-end-to-end)
6. [Các Kênh Giao Tiếp](#các-kênh-giao-tiếp)
7. [Cơ Chế Reconnect](#cơ-chế-reconnect)

---

## 🎯 Tổng Quan

Project sử dụng **WebSocket với STOMP protocol** để thực hiện real-time notification giữa server và client. Hệ thống hỗ trợ:

- ✅ **User-specific notifications** (gửi đến 1 user cụ thể)
- ✅ **Broadcast notifications** (gửi đến tất cả users)
- ✅ **Auto-reconnect** khi mất kết nối
- ✅ **JWT Authentication** cho bảo mật
- ✅ **SockJS fallback** cho trình duyệt không hỗ trợ WebSocket

---

## 🏗️ Kiến Trúc Tổng Thể

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                 │
├─────────────────────────────────────────────────────────────────┤
│  React Component                                                 │
│       ↓                                                          │
│  useWebSocketNotifications Hook                                  │
│       ↓                                                          │
│  WebSocketService                                                │
│       ↓                                                          │
│  STOMP Client (with SockJS)                                      │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     │ WebSocket Connection
                     │ ws://localhost:9090/ws
                     │
┌────────────────────┴────────────────────────────────────────────┐
│                         BACKEND                                  │
├─────────────────────────────────────────────────────────────────┤
│  WebSocketConfig                                                 │
│       ↓                                                          │
│  Spring WebSocket Message Broker                                │
│       ↓                                                          │
│  WebSocketNotificationService                                    │
│       ↓                                                          │
│  SimpMessagingTemplate                                           │
│       ↓                                                          │
│  Notification Services (NotificationServiceImpl, etc.)          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔵 Luồng Backend

### 1. **Cấu hình WebSocket** (`WebSocketConfig.java`)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable message broker
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

**Vai trò:**
- `/ws` - Endpoint để client kết nối WebSocket
- `/topic` - Prefix cho broadcast messages (gửi đến tất cả)
- `/queue` - Prefix cho user-specific messages
- `/user` - Prefix để Spring tự động route message đến đúng user
- `withSockJS()` - Fallback cho browser không hỗ trợ WebSocket thuần

### 2. **Service Gửi Notification** (`WebSocketNotificationService.java`)

```java
@Service
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    // Gửi notification đến 1 user cụ thể
    public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            message
        );
    }
    
    // Broadcast đến tất cả users
    public void broadcastNotification(WebSocketNotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
    
    // Gửi số lượng notification chưa đọc
    public void sendNotificationCount(Long userId, Long unreadCount) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notification-count",
            unreadCount
        );
    }
}
```

### 3. **Tạo và Gửi Notification** (`NotificationServiceImpl.java`)

```java
@Service
public class NotificationServiceImpl implements INotificationService {
    
    @Autowired
    private WebSocketNotificationService webSocketNotificationService;
    
    @Override
    public NotificationRes createNotification(Long userId, String message, ENotificationType type) {
        // 1. Lưu notification vào database
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        notification = notificationRepository.save(notification);
        
        // 2. Gửi real-time qua WebSocket
        WebSocketNotificationMessage wsMessage = 
            convertToWebSocketMessage(notification, "NEW_NOTIFICATION");
        webSocketNotificationService.sendNotificationToUser(userId, wsMessage);
        
        // 3. Gửi cập nhật số lượng chưa đọc
        Long unreadCount = notificationRepository.countByUserAndIsRead(user, false);
        webSocketNotificationService.sendNotificationCount(userId, unreadCount);
        
        return notificationMapper.toResponse(notification);
    }
}
```

**Luồng Backend chi tiết:**

```
1. Business Logic (Task, Project, Report, etc.)
   ↓
2. NotificationServiceImpl.createNotification()
   ↓
3. Save to Database
   ↓
4. WebSocketNotificationService.sendNotificationToUser()
   ↓
5. SimpMessagingTemplate.convertAndSendToUser()
   ↓
6. Spring Message Broker
   ↓
7. Tìm WebSocket Session của user
   ↓
8. Gửi message qua WebSocket connection
   ↓
9. Client nhận được notification
```

---

## 🟢 Luồng Frontend

### 1. **WebSocket Service** (`websocket.service.ts`)

Đây là core service quản lý kết nối WebSocket:

```typescript
class WebSocketService {
  private client: Client | null = null;
  private isConnected = false;
  
  // Connect đến server
  connect(userId: number, token?: string) {
    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:9090/ws';
    
    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      
      // Gửi JWT token khi connect
      connectHeaders: token ? {
        Authorization: `Bearer ${token}`
      } : {},
      
      // Callback khi kết nối thành công
      onConnect: () => {
        this.isConnected = true;
        this.subscribeToTopics(userId);
      },
      
      // Tự động reconnect
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
    });
    
    this.client.activate();
  }
  
  // Subscribe các kênh nhận notification
  private subscribeToTopics(userId: number) {
    // Kênh 1: Nhận notification mới
    this.client.subscribe(`/user/queue/notifications`, (message) => {
      const notification = JSON.parse(message.body);
      this.notifyNotificationCallbacks(notification);
    });
    
    // Kênh 2: Nhận số lượng chưa đọc
    this.client.subscribe(`/user/queue/notification-count`, (message) => {
      const count = JSON.parse(message.body);
      this.notifyNotificationCountCallbacks(count);
    });
    
    // Kênh 3: Nhận cập nhật (đã đọc, đã xóa)
    this.client.subscribe(`/user/queue/notification-updates`, (message) => {
      const notification = JSON.parse(message.body);
      this.notifyNotificationCallbacks(notification);
    });
    
    // Kênh 4: Broadcast (optional)
    this.client.subscribe(`/topic/notifications`, (message) => {
      const notification = JSON.parse(message.body);
      this.notifyNotificationCallbacks(notification);
    });
  }
}
```

### 2. **React Hook** (`useWebSocketNotifications.ts`)

Hook này integrate WebSocket service vào React components:

```typescript
export const useWebSocketNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isConnected, setIsConnected] = useState(false);
  const { user } = useAuth();
  const { addToast } = useToast();
  
  // Xử lý notification mới
  const handleNotification = useCallback((notification) => {
    // Thêm vào danh sách
    setNotifications(prev => [notification, ...prev]);
    
    // Hiển thị toast
    if (notification.action === 'NEW_NOTIFICATION') {
      addToast({
        title: notification.title,
        description: notification.message,
      });
      
      // Phát âm thanh
      const audio = new Audio('/notification-sound.mp3');
      audio.play();
    }
  }, [addToast]);
  
  // Kết nối WebSocket khi user login
  useEffect(() => {
    if (user && user.id) {
      const token = localStorage.getItem('accessToken');
      
      // Đăng ký callbacks
      webSocketService.onNotification(handleNotification);
      webSocketService.onNotificationCount(handleNotificationCount);
      webSocketService.onConnectionChange(handleConnectionChange);
      
      // Connect
      webSocketService.connect(user.id, token);
      
      // Cleanup khi unmount
      return () => {
        webSocketService.disconnect();
      };
    }
  }, [user]);
  
  return {
    notifications,
    unreadCount,
    isConnected,
    markAsRead,
  };
};
```

### 3. **Sử dụng trong Component**

```typescript
function NotificationBell() {
  const { 
    notifications, 
    unreadCount, 
    isConnected,
    markAsRead 
  } = useWebSocketNotifications();
  
  return (
    <div>
      <Badge count={unreadCount}>
        <BellIcon />
      </Badge>
      
      {isConnected ? (
        <span>🟢 Connected</span>
      ) : (
        <span>🔴 Disconnected</span>
      )}
      
      <NotificationList 
        notifications={notifications}
        onRead={markAsRead}
      />
    </div>
  );
}
```

---

## 🔄 Luồng Hoạt Động End-to-End

### Scenario: User A tạo task mới và assign cho User B

```
┌─────────────────────────────────────────────────────────────────┐
│                    STEP 1: User A tạo task                       │
└─────────────────────────────────────────────────────────────────┘

User A (Frontend)
  ↓
  POST /api/v1/tasks
  ↓
TaskController.createTask()
  ↓
TaskServiceImpl.createTask()
  ↓
Save Task to Database

┌─────────────────────────────────────────────────────────────────┐
│              STEP 2: Backend tạo notification                    │
└─────────────────────────────────────────────────────────────────┘

TaskServiceImpl (sau khi save task)
  ↓
NotificationServiceImpl.createNotification(
  userId = User B's ID,
  message = "User A assigned you a new task",
  type = TASK_ASSIGNED
)
  ↓
Save Notification to Database
  ↓
WebSocketNotificationService.sendNotificationToUser(
  userB.id,
  notification
)

┌─────────────────────────────────────────────────────────────────┐
│          STEP 3: Spring Message Broker xử lý                     │
└─────────────────────────────────────────────────────────────────┘

SimpMessagingTemplate.convertAndSendToUser(
  "userB.id",
  "/queue/notifications",
  notification
)
  ↓
Spring tìm WebSocket Session của User B
  ↓
Destination: /user/{userB.id}/queue/notifications
  ↓
Gửi message qua WebSocket connection

┌─────────────────────────────────────────────────────────────────┐
│            STEP 4: User B's Browser nhận message                 │
└─────────────────────────────────────────────────────────────────┘

WebSocket Client (User B's browser)
  ↓
STOMP Frame received
  ↓
WebSocketService.subscribeToTopics() callback
  ↓
Parse JSON message
  ↓
Call notificationCallbacks

┌─────────────────────────────────────────────────────────────────┐
│         STEP 5: React Component cập nhật UI                      │
└─────────────────────────────────────────────────────────────────┘

useWebSocketNotifications.handleNotification()
  ↓
setNotifications([new notification, ...old])
  ↓
addToast("User A assigned you a new task")
  ↓
Play notification sound
  ↓
Update badge count
  ↓
User B thấy notification ngay lập tức! 🎉
```

**Timeline:**
- `T+0ms`: User A click "Create Task"
- `T+50ms`: Backend save task và notification
- `T+100ms`: WebSocket gửi message
- `T+120ms`: User B's browser nhận message
- `T+150ms`: React component re-render
- **Total: ~150ms** - Real-time!

---

## 📡 Các Kênh Giao Tiếp

### 1. **User-Specific Channels** (Point-to-Point)

#### `/user/queue/notifications`
```typescript
// Backend gửi:
messagingTemplate.convertAndSendToUser(
  userId.toString(),
  "/queue/notifications",
  notification
);

// Frontend subscribe:
client.subscribe("/user/queue/notifications", (message) => {
  const notification = JSON.parse(message.body);
  // Xử lý notification mới
});
```

**Ví dụ notification:**
```json
{
  "id": 123,
  "title": "New Task Assigned",
  "message": "User A assigned you a new task: Fix Bug #456",
  "type": "TASK_ASSIGNED",
  "referenceId": 456,
  "referenceType": "TASK",
  "triggeredById": 1,
  "triggeredByName": "User A",
  "timestamp": "2024-11-29T10:30:00",
  "isRead": false,
  "action": "NEW_NOTIFICATION"
}
```

#### `/user/queue/notification-count`
```typescript
// Backend gửi:
messagingTemplate.convertAndSendToUser(
  userId.toString(),
  "/queue/notification-count",
  5  // số notification chưa đọc
);

// Frontend subscribe:
client.subscribe("/user/queue/notification-count", (message) => {
  const count = JSON.parse(message.body);
  setUnreadCount(count); // Update badge
});
```

#### `/user/queue/notification-updates`
```typescript
// Backend gửi khi user đánh dấu đã đọc:
messagingTemplate.convertAndSendToUser(
  userId.toString(),
  "/queue/notification-updates",
  {
    id: 123,
    action: "NOTIFICATION_READ"
  }
);

// Frontend subscribe:
client.subscribe("/user/queue/notification-updates", (message) => {
  const update = JSON.parse(message.body);
  // Cập nhật trạng thái notification
});
```

### 2. **Broadcast Channel** (Pub-Sub)

#### `/topic/notifications`
```typescript
// Backend broadcast đến tất cả users:
messagingTemplate.convertAndSend(
  "/topic/notifications",
  {
    title: "System Maintenance",
    message: "System will be down at 2 AM",
    type: "SYSTEM_ANNOUNCEMENT"
  }
);

// Tất cả clients đang kết nối sẽ nhận được:
client.subscribe("/topic/notifications", (message) => {
  const announcement = JSON.parse(message.body);
  showSystemNotification(announcement);
});
```

**Use cases:**
- System announcements
- Emergency alerts
- Feature releases
- Maintenance notifications

---

## 🔁 Cơ Chế Reconnect

### Automatic Reconnection

Frontend có built-in auto-reconnect mechanism:

```typescript
class WebSocketService {
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  
  private handleReconnect(userId: number, token?: string) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      
      setTimeout(() => {
        console.log(`Reconnecting... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
        this.connect(userId, token);
      }, this.reconnectDelay * this.reconnectAttempts);
      
    } else {
      console.error('Max reconnection attempts reached');
      // Có thể show notification cho user
    }
  }
}
```

**Reconnect Strategy:**
- Attempt 1: sau 3s
- Attempt 2: sau 6s (3s × 2)
- Attempt 3: sau 9s (3s × 3)
- Attempt 4: sau 12s (3s × 4)
- Attempt 5: sau 15s (3s × 5)
- Sau 5 lần thất bại → dừng

### Heartbeat

```typescript
this.client = new Client({
  heartbeatIncoming: 10000,  // Expect heartbeat từ server mỗi 10s
  heartbeatOutgoing: 10000,  // Gửi heartbeat đến server mỗi 10s
});
```

**Mục đích:**
- Giữ kết nối alive
- Phát hiện connection loss sớm
- Tránh timeout từ proxy/firewall

### Connection States

```typescript
export const useWebSocketNotifications = () => {
  const [isConnected, setIsConnected] = useState(false);
  
  useEffect(() => {
    webSocketService.onConnectionChange((connected) => {
      setIsConnected(connected);
      
      if (connected) {
        console.log('✅ WebSocket connected');
      } else {
        console.log('❌ WebSocket disconnected, will retry...');
      }
    });
  }, []);
  
  return { isConnected };
};
```

---

## 🔐 Security & Authentication

### JWT Token Authentication

```typescript
// Frontend gửi token khi connect
this.client = new Client({
  connectHeaders: {
    Authorization: `Bearer ${token}`
  }
});
```

**Backend extract user từ token:**
```java
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    // Spring tự động extract Principal từ JWT
    // và route message đến đúng user session
}
```

### User Session Management

```java
// Spring quản lý mapping: userId → WebSocket Sessions
public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
    // Spring tự động tìm tất cả sessions của user này
    // (user có thể mở nhiều tabs/devices)
    messagingTemplate.convertAndSendToUser(
        userId.toString(),
        "/queue/notifications",
        message
    );
}
```

---

## 📊 Message Flow Diagram

```
Backend                          Message Broker                    Frontend
───────                          ──────────────                    ────────

[Service]                                                          [Browser]
    │                                                                  │
    │  convertAndSendToUser()                                         │
    │────────────────────>                                            │
    │                    [Find User Sessions]                         │
    │                          │                                      │
    │                          │  STOMP Frame                         │
    │                          │────────────────────────────────────> │
    │                          │                                      │
    │                          │                            [Parse JSON]
    │                          │                                      │
    │                          │                          [Fire Callbacks]
    │                          │                                      │
    │                          │                          [Update State]
    │                          │                                      │
    │                          │                             [Show UI]
    │                          │                                      │
```

---

## 🎯 Best Practices

### 1. **Xử lý Errors**
```typescript
try {
  webSocketService.connect(userId, token);
} catch (error) {
  console.error('Failed to connect WebSocket:', error);
  // Fallback: polling hoặc thông báo user
}
```

### 2. **Cleanup**
```typescript
useEffect(() => {
  webSocketService.connect(userId, token);
  
  return () => {
    // QUAN TRỌNG: disconnect khi unmount
    webSocketService.disconnect();
  };
}, [userId, token]);
```

### 3. **Debounce Notifications**
```typescript
const handleNotification = useMemo(
  () => debounce((notification) => {
    addToast(notification);
  }, 500),
  []
);
```

### 4. **Graceful Degradation**
```typescript
if (!isConnected) {
  // Fallback: polling REST API
  const interval = setInterval(() => {
    fetchNotifications();
  }, 30000);
  
  return () => clearInterval(interval);
}
```

---

## 🐛 Debugging

### Frontend Console Logs
```typescript
// Enable debug mode
this.client = new Client({
  debug: (str) => {
    console.log('[WebSocket Debug]', str);
  }
});
```

### Check Connection Status
```typescript
console.log('Connected:', webSocketService.isConnected);
console.log('Client:', webSocketService.client);
```

### Backend Logs
```java
@Slf4j
@Service
public class WebSocketNotificationService {
    
    public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
        log.info("Sending notification to user {}: {}", userId, message);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", message);
    }
}
```

---

## 📚 Tài Liệu Liên Quan

- [STOMP Protocol](../docs/websocket/STOMP_PROTOCOL_EXPLAINED.md)
- [Message Broker](../docs/websocket/MESSAGE_BROKER_EXPLAINED.md)
- [Session Routing](../docs/websocket/SESSION_ROUTING_EXPLAINED.md)
- [Troubleshooting](../docs/websocket/TROUBLESHOOTING.md)

---

## ✅ Tóm Tắt

### Backend Flow:
1. Business logic trigger notification
2. Save notification to database
3. `WebSocketNotificationService.sendNotificationToUser()`
4. `SimpMessagingTemplate` gửi message qua WebSocket
5. Spring Message Broker route đến đúng user session

### Frontend Flow:
1. User login → `useWebSocketNotifications` hook mount
2. `WebSocketService.connect()` với JWT token
3. Subscribe các channels: `/user/queue/notifications`, etc.
4. Nhận message → Parse JSON → Fire callbacks
5. React component update state → Re-render UI
6. User thấy notification real-time!

### Key Technologies:
- **Backend:** Spring WebSocket + STOMP + SimpMessagingTemplate
- **Frontend:** @stomp/stompjs + SockJS + React Hooks
- **Protocol:** STOMP over WebSocket/SockJS
- **Authentication:** JWT Bearer Token

🎉 **Kết quả:** Real-time notification với latency ~100-200ms!
