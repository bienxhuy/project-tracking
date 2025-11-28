# WebSocketConfig.java - Chi tiết giải thích

## 📁 File Location
```
backend/src/main/java/POSE_Project_Tracking/Blog/config/WebSocketConfig.java
```

## 🎯 Mục đích
Configuration class để thiết lập WebSocket communication sử dụng STOMP (Simple Text Oriented Messaging Protocol) over SockJS.

---

## 📝 Code với Giải thích chi tiết

### 1. Package và Imports

```java
package POSE_Project_Tracking.Blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
```

**Giải thích:**
- `@Configuration`: Đánh dấu class này là Spring Configuration class
- `EnableWebSocketMessageBroker`: Annotation để enable WebSocket message handling với STOMP
- `MessageBrokerRegistry`: Interface để configure message broker
- `StompEndpointRegistry`: Interface để register STOMP endpoints
- `WebSocketMessageBrokerConfigurer`: Interface để customize WebSocket configuration

---

### 2. Class Declaration

```java
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
```

**Giải thích từng annotation:**

#### `@Configuration`
- **Mục đích**: Báo cho Spring biết đây là configuration class
- **Hoạt động**: Spring sẽ scan và load configuration này khi app khởi động
- **Tương đương**: Như file XML config trong Spring cũ

#### `@EnableWebSocketMessageBroker`
- **Mục đích**: Enable WebSocket message handling backed by a message broker
- **Chức năng**: 
  - Enable WebSocket server
  - Enable STOMP protocol
  - Configure message broker để route messages
- **Kết quả**: App có thể nhận và gửi messages qua WebSocket

#### `@RequiredArgsConstructor` (Lombok)
- **Mục đích**: Tự động generate constructor cho final fields
- **Lợi ích**: Giảm boilerplate code
- **Note**: Trong class này không có final fields nên constructor rỗng

#### `implements WebSocketMessageBrokerConfigurer`
- **Mục đích**: Interface cung cấp methods để customize WebSocket config
- **Methods override**:
  - `configureMessageBroker()`: Configure message broker
  - `registerStompEndpoints()`: Register STOMP endpoints

---

### 3. Configure Message Broker

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
    // Enable simple broker for /topic and /queue destinations
    config.enableSimpleBroker("/topic", "/queue");
    
    // Set prefix for messages bound for @MessageMapping methods
    config.setApplicationDestinationPrefixes("/app");
    
    // Set prefix for user-specific destinations
    config.setUserDestinationPrefix("/user");
}
```

#### **Line 1: `config.enableSimpleBroker("/topic", "/queue")`**

**Chi tiết:**
```
enableSimpleBroker(String... destinations)
```

**Giải thích:**
- **Simple Broker**: In-memory message broker (không cần external server như RabbitMQ)
- **Hoạt động**: 
  - Broker sẽ route messages đến clients đã subscribe
  - Messages gửi đến `/topic` hoặc `/queue` sẽ được broker handle
  
**Phân biệt `/topic` vs `/queue`:**

| Prefix | Loại | Mô tả | Use Case |
|--------|------|-------|----------|
| `/topic` | Broadcast | Gửi đến TẤT CẢ subscribers | System announcements, global notifications |
| `/queue` | Point-to-Point | Gửi đến MỘT subscriber cụ thể | User-specific notifications |

**Ví dụ:**
```java
// Broadcast to all subscribers of /topic/notifications
messagingTemplate.convertAndSend("/topic/notifications", message);

// Send to specific user's queue
messagingTemplate.convertAndSendToUser("123", "/queue/notifications", message);
```

**Flow:**
```
Client 1 subscribe: /topic/notifications
Client 2 subscribe: /topic/notifications
Client 3 subscribe: /topic/notifications

Server send to: /topic/notifications
   ↓
Broker routes to → Client 1 ✅
                → Client 2 ✅
                → Client 3 ✅
```

#### **Line 2: `config.setApplicationDestinationPrefixes("/app")`**

**Giải thích:**
- **Mục đích**: Prefix cho messages TỪ client GỬI ĐẾN server
- **Hoạt động**: Client gửi message đến `/app/*` sẽ được route đến `@MessageMapping` methods

**Flow:**
```
Client gửi message:
  destination: "/app/chat"
  ↓
Spring tìm method:
  @MessageMapping("/chat")
  public void handleChat(...) { ... }
  ↓
Method được execute
```

**Ví dụ code:**

**Backend:**
```java
@MessageMapping("/chat")  // Mapped to /app/chat
public void handleChat(ChatMessage message) {
    // Process message from client
}
```

**Frontend:**
```javascript
// Client gửi message đến server
stompClient.send("/app/chat", {}, JSON.stringify({
    message: "Hello"
}));
```

**Note**: Trong project này chưa có `@MessageMapping` vì chỉ cần server → client communication (notifications).

#### **Line 3: `config.setUserDestinationPrefix("/user")`**

**Giải thích:**
- **Mục đích**: Prefix cho user-specific destinations
- **Hoạt động**: Khi gửi đến user, Spring tự động thêm session ID

**Magic của Spring:**

**Backend gửi:**
```java
messagingTemplate.convertAndSendToUser(
    "userId123",          // User ID
    "/queue/notifications", // Destination
    message               // Payload
);
```

**Spring tự động convert thành:**
```
/user/userId123/queue/notifications
```

**Client subscribe:**
```javascript
// Client subscribe với /user prefix
stompClient.subscribe("/user/queue/notifications", (message) => {
    // Handle notification
});
```

**Spring matching:**
```
Client session ID: session-abc-123
User ID: userId123

Spring maps:
  /user/queue/notifications 
  → /user/userId123/queue/notifications
  → session-abc-123
```

**Lợi ích:**
- ✅ Security: User chỉ nhận messages của chính họ
- ✅ Simplicity: Client không cần biết user ID
- ✅ Multi-session: Cùng user có thể có nhiều sessions (nhiều tabs)

---

### 4. Register STOMP Endpoints

```java
@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
}
```

#### **Line 1: `registry.addEndpoint("/ws")`**

**Giải thích:**
- **Endpoint**: URL để client kết nối WebSocket
- **Full URL**: `ws://localhost:9090/ws` (or `wss://` for SSL)
- **Hoạt động**: Đây là handshake endpoint

**Connection Flow:**
```
1. Client connect: http://localhost:9090/ws
2. HTTP Handshake (Upgrade request)
3. Protocol switch: HTTP → WebSocket
4. Persistent connection established
```

**HTTP Upgrade Request:**
```http
GET /ws HTTP/1.1
Host: localhost:9090
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Sec-WebSocket-Version: 13
```

**Response:**
```http
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=
```

#### **Line 2: `.setAllowedOriginPatterns("*")`**

**Giải thích:**
- **CORS**: Cross-Origin Resource Sharing configuration
- **`"*"`**: Allow tất cả origins (dev only)
- **Security Risk**: Không nên dùng `*` trong production

**Production Config:**
```java
.setAllowedOriginPatterns(
    "https://yourdomain.com",
    "https://app.yourdomain.com"
)
```

**Hoặc từ environment variable:**
```java
@Value("${cors.allowed-origins}")
private String[] allowedOrigins;

registry.addEndpoint("/ws")
    .setAllowedOriginPatterns(allowedOrigins)
    .withSockJS();
```

**CORS Preflight:**
```http
OPTIONS /ws HTTP/1.1
Origin: http://localhost:3000
Access-Control-Request-Method: POST
Access-Control-Request-Headers: content-type
```

**Spring Response:**
```http
HTTP/1.1 200 OK
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: content-type
```

#### **Line 3: `.withSockJS()`**

**Giải thích:**
- **SockJS**: WebSocket emulation library
- **Mục đích**: Fallback cho browsers không support WebSocket

**SockJS Transport Hierarchy:**
```
1. WebSocket (preferred)
   ↓ (if fails)
2. HTTP Streaming
   ↓ (if fails)
3. HTTP Long Polling
   ↓ (if fails)
4. XHR Streaming
   ↓ (if fails)
5. XDR Streaming (IE)
   ↓ (if fails)
6. iFrame eventsource
   ↓ (if fails)
7. iFrame htmlfile
   ↓ (if fails)
8. XHR polling
```

**SockJS Endpoints được tự động tạo:**
```
GET  /ws/info        # Get transport info
GET  /ws/{server}/{session}/websocket
POST /ws/{server}/{session}/xhr
POST /ws/{server}/{session}/xhr_streaming
GET  /ws/{server}/{session}/eventsource
GET  /ws/{server}/{session}/htmlfile
POST /ws/{server}/{session}/xhr_send
POST /ws/{server}/{session}/jsonp_send
```

**Browser Compatibility:**

| Browser | WebSocket | SockJS Fallback |
|---------|-----------|-----------------|
| Chrome 16+ | ✅ | ✅ |
| Firefox 11+ | ✅ | ✅ |
| Safari 6+ | ✅ | ✅ |
| IE 10+ | ✅ | ✅ |
| IE 8-9 | ❌ | ✅ (uses XDR) |

**Without SockJS:**
```java
registry.addEndpoint("/ws")
    .setAllowedOriginPatterns("*");
// Only WebSocket, no fallback
```

**With SockJS:**
```java
registry.addEndpoint("/ws")
    .setAllowedOriginPatterns("*")
    .withSockJS();
// WebSocket + multiple fallbacks
```

---

## 🔄 Complete Message Flow

### Flow 1: Server → Client Notification

```
┌──────────────────────────────────────────────────────────────┐
│  1. Backend creates notification                             │
│     NotificationServiceImpl.createNotification()             │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  2. Save to database                                         │
│     notificationRepository.save(notification)                │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  3. Send WebSocket message                                   │
│     webSocketNotificationService.sendNotificationToUser()    │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  4. SimpMessagingTemplate routes message                     │
│     messagingTemplate.convertAndSendToUser(                  │
│       userId, "/queue/notifications", message)               │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  5. Message Broker receives message                          │
│     Spring's SimpleBroker                                    │
│     Destination: /user/{userId}/queue/notifications          │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  6. Broker finds subscribed sessions                         │
│     User may have multiple sessions (multiple tabs)          │
│     Session IDs: [session-1, session-2, session-3]           │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  7. Send via WebSocket to all user sessions                  │
│     WebSocket frame sent to each connection                  │
│     Frame: STOMP MESSAGE                                     │
│     Destination: /user/queue/notifications                   │
│     Content-Type: application/json                           │
│     Body: {"id": 123, "title": "..."}                        │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  8. Client receives message                                  │
│     stompClient.subscribe("/user/queue/notifications")       │
│     Callback executed with message                           │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  9. React component updates                                  │
│     useWebSocketNotification hook updates state              │
│     UI re-renders with new notification                      │
└──────────────────────────────────────────────────────────────┘
```

### Flow 2: Client → Server (if needed)

```
┌──────────────────────────────────────────────────────────────┐
│  1. Client sends message                                     │
│     stompClient.send("/app/action", {}, JSON.stringify(...)) │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  2. Message arrives at /ws endpoint                          │
│     WebSocket frame received                                 │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  3. Spring routes to @MessageMapping                         │
│     Finds method with @MessageMapping("/action")             │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  4. Method processes message                                 │
│     @MessageMapping("/action")                               │
│     public void handleAction(Message msg) { ... }            │
└────────────────┬─────────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────────┐
│  5. Optionally send response                                 │
│     messagingTemplate.convertAndSend(...)                    │
└──────────────────────────────────────────────────────────────┘
```

---

## 🔒 Security Considerations

### 1. CORS Configuration

**Current (Development):**
```java
.setAllowedOriginPatterns("*")  // ⚠️ Allow all origins
```

**Production:**
```java
.setAllowedOriginPatterns(
    "https://yourdomain.com",
    "https://app.yourdomain.com"
)
```

### 2. Authentication

**Add JWT authentication:**

```java
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    public void configureClientInboundChannel(ChannelInterceptorAdapter registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = 
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        String jwt = token.substring(7);
                        if (jwtTokenProvider.validateToken(jwt)) {
                            String username = jwtTokenProvider.getUsernameFromToken(jwt);
                            accessor.setUser(() -> username);
                        }
                    }
                }
                return message;
            }
        });
    }
}
```

---

## 📊 Performance Tuning

### 1. Message Size Limits

```java
@Override
public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration
        .setMessageSizeLimit(128 * 1024)     // 128KB max message size
        .setSendBufferSizeLimit(512 * 1024)  // 512KB send buffer
        .setSendTimeLimit(20000);            // 20 seconds timeout
}
```

### 2. Thread Pool Configuration

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue")
        .setTaskScheduler(taskScheduler())  // Custom scheduler
        .setHeartbeatValue(new long[]{10000, 10000}); // 10s heartbeat
}

@Bean
public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    scheduler.setThreadNamePrefix("websocket-");
    scheduler.initialize();
    return scheduler;
}
```

---

## 🧪 Testing

### Test WebSocket Connection

```bash
# Using wscat (npm install -g wscat)
wscat -c ws://localhost:9090/ws

# Connected to ws://localhost:9090/ws
> CONNECT
> accept-version:1.2
> 
> ^@

# Subscribe to topic
> SUBSCRIBE
> id:sub-0
> destination:/topic/notifications
> 
> ^@
```

---

## 📚 References

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol Spec](https://stomp.github.io/)
- [SockJS Protocol](https://github.com/sockjs/sockjs-protocol)

---

**Tóm tắt:**
- `WebSocketConfig` configure WebSocket server với STOMP protocol
- Support 2 loại destinations: `/topic` (broadcast) và `/queue` (point-to-point)
- SockJS fallback cho browsers không support WebSocket
- Simple in-memory broker cho < 1000 concurrent users
