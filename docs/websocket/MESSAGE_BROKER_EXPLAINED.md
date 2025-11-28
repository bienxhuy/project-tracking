# Message Broker - Giải Thích Chi Tiết

## 🎯 Message Broker Là Gì?

**Message Broker** là một middleware component đóng vai trò **trung gian** để **route messages** giữa **producers** (người gửi) và **consumers** (người nhận).

### Analogy (Ví dụ thực tế)

Tưởng tượng Message Broker như **bưu điện**:

```
Người gửi (Producer)
  ↓ Gửi thư
Bưu điện (Message Broker)
  ↓ Phân loại và route
  ↓ Dựa trên địa chỉ
Người nhận (Consumer)
  ↓ Nhận thư
```

---

## 🔄 Message Broker trong WebSocket

### Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                      Backend                             │
│                                                          │
│  ┌──────────────────────────────────────────────┐      │
│  │  NotificationService (Producer)              │      │
│  │  - Creates notification                      │      │
│  │  - Calls WebSocketNotificationService        │      │
│  └────────────────┬─────────────────────────────┘      │
│                   │                                      │
│                   │ Send message                         │
│                   ↓                                      │
│  ┌──────────────────────────────────────────────┐      │
│  │  SimpMessagingTemplate                       │      │
│  │  - convertAndSendToUser()                    │      │
│  └────────────────┬─────────────────────────────┘      │
│                   │                                      │
│                   ↓                                      │
│  ┌──────────────────────────────────────────────┐      │
│  │         MESSAGE BROKER                       │      │
│  │  ┌────────────────────────────────────┐     │      │
│  │  │  SimpleBroker (In-Memory)          │     │      │
│  │  │  - /topic/* (Broadcast)            │     │      │
│  │  │  - /queue/* (Point-to-point)       │     │      │
│  │  │  - /user/* (User-specific)         │     │      │
│  │  └────────────────────────────────────┘     │      │
│  │                                              │      │
│  │  Responsibilities:                           │      │
│  │  1. Store subscriptions                      │      │
│  │  2. Match destinations                       │      │
│  │  3. Route messages                           │      │
│  │  4. Manage sessions                          │      │
│  └────────────────┬─────────────────────────────┘      │
│                   │                                      │
│                   │ Deliver to subscribers               │
│                   ↓                                      │
│  ┌──────────────────────────────────────────────┐      │
│  │  WebSocket Sessions (Consumers)              │      │
│  │  - Session 1: User 123, Tab 1               │      │
│  │  - Session 2: User 123, Tab 2               │      │
│  │  - Session 3: User 456, Tab 1               │      │
│  └────────────────┬─────────────────────────────┘      │
│                   │                                      │
└───────────────────┼──────────────────────────────────────┘
                    │
                    │ WebSocket Connection
                    ↓
┌─────────────────────────────────────────────────────────┐
│                    Frontend                              │
│  - Receives messages                                     │
│  - Updates UI                                            │
└─────────────────────────────────────────────────────────┘
```

---

## 🎯 Vai Trò của Message Broker

### 1. **Message Routing** (Route tin nhắn)

Message Broker quyết định message được gửi đến **đâu** dựa trên **destination**.

**Example:**

```java
// Producer gửi message
simpMessagingTemplate.convertAndSendToUser("123", "/queue/notifications", msg);
```

**Message Broker làm gì:**

```
1. Nhận message với:
   - userId: "123"
   - destination: "/queue/notifications"
   - payload: msg

2. Expand destination:
   /queue/notifications → /user/123/queue/notifications

3. Lookup subscriptions:
   "Tìm tất cả sessions đã subscribe đến /user/123/queue/notifications"

4. Found:
   - Session ABC (User 123, Tab 1) ✅
   - Session XYZ (User 123, Tab 2) ✅

5. Route message:
   - Send to Session ABC ✅
   - Send to Session XYZ ✅
```

---

### 2. **Subscription Management** (Quản lý subscriptions)

Message Broker **lưu trữ** tất cả **subscriptions** của clients.

**Example:**

```typescript
// Client subscribes
client.subscribe('/user/queue/notifications', (message) => {
  console.log('Received:', message);
});
```

**Message Broker stores:**

```
Subscription Registry:
{
  destination: "/user/123/queue/notifications",
  sessionId: "ABC",
  subscriptionId: "sub-0",
  userId: "123"
}
```

**When message arrives:**

```
Message Broker checks:
  "Destination /user/123/queue/notifications có ai subscribe không?"
  
Found:
  - Session ABC with subscription sub-0 ✅
  
Action:
  - Send message to Session ABC ✅
```

---

### 3. **Destination Patterns** (Phân loại destinations)

Message Broker hỗ trợ **nhiều loại destinations** với **routing logic khác nhau**.

#### Pattern 1: `/topic/*` - Broadcast (Fan-out)

**Purpose:** Gửi đến **tất cả** subscribers

```java
// Backend gửi announcement
simpMessagingTemplate.convertAndSend("/topic/announcements", "Server maintenance in 10 minutes");
```

**Message Broker routing:**

```
Destination: /topic/announcements

Subscribers:
  - Session 1 (User 123) ✅
  - Session 2 (User 456) ✅
  - Session 3 (User 789) ✅

Action:
  Send to ALL subscribers → Broadcast ✅
```

---

#### Pattern 2: `/queue/*` - Point-to-Point

**Purpose:** Gửi đến **một** consumer (load balancing)

```java
// Backend gửi task
simpMessagingTemplate.convertAndSend("/queue/tasks", task);
```

**Message Broker routing:**

```
Destination: /queue/tasks

Subscribers:
  - Worker 1 ✅
  - Worker 2
  - Worker 3

Action:
  Send to ONE worker (round-robin) → Load balance ✅
```

---

#### Pattern 3: `/user/*` - User-Specific

**Purpose:** Gửi đến **user cụ thể** (tất cả sessions của user đó)

```java
// Backend gửi notification
simpMessagingTemplate.convertAndSendToUser("123", "/queue/notifications", msg);
```

**Message Broker routing:**

```
Destination: /user/123/queue/notifications

Lookup:
  "Find all sessions của user 123"

Found:
  - Session ABC (User 123, Desktop) ✅
  - Session XYZ (User 123, Mobile) ✅

Action:
  Send to ALL sessions của user 123 ✅
```

---

### 4. **Message Storage** (Lưu trữ messages - Optional)

Một số Message Brokers có thể **lưu trữ messages** tạm thời.

**SimpleBroker (Spring default):**
- ❌ **Không lưu trữ** - In-memory, messages chỉ tồn tại trong RAM
- ❌ Messages mất nếu server restart
- ✅ Nhanh, đơn giản, phù hợp cho dev

**External Brokers (Redis, RabbitMQ):**
- ✅ **Có lưu trữ** - Persistent storage
- ✅ Messages không mất khi server restart
- ✅ Hỗ trợ clustering, load balancing
- ✅ Phù hợp cho production

---

### 5. **Session Management** (Quản lý sessions)

Message Broker track tất cả **active sessions** và **subscriptions**.

**Internal Registry:**

```
SimpUserRegistry:
  User 123:
    Session ABC:
      - subscriptionId: sub-0
      - destination: /user/123/queue/notifications
      - created: 2025-11-17T10:00:00
    Session XYZ:
      - subscriptionId: sub-1
      - destination: /user/123/queue/notifications
      - created: 2025-11-17T10:05:00
      
  User 456:
    Session PQR:
      - subscriptionId: sub-2
      - destination: /user/456/queue/notifications
      - created: 2025-11-17T10:10:00
```

**When session disconnects:**

```
Session ABC disconnected
  ↓
Message Broker removes:
  - Subscription sub-0
  - Session ABC from User 123
  ↓
Updated registry:
  User 123:
    Session XYZ (only) ✅
```

---

## 📊 SimpleBroker vs External Broker

### SimpleBroker (Spring Default)

**Configuration:**

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Enable SimpleBroker
    registry.enableSimpleBroker("/topic", "/queue");
    
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
}
```

**Characteristics:**

| Feature | SimpleBroker |
|---------|--------------|
| **Type** | In-memory |
| **Persistence** | ❌ None (messages in RAM) |
| **Clustering** | ❌ Not supported |
| **Scalability** | ❌ Single server only |
| **Setup** | ✅ Zero configuration |
| **Performance** | ✅ Very fast (in-memory) |
| **Use Case** | Development, small apps |

**Pros:**
- ✅ Cực kỳ đơn giản, zero config
- ✅ Nhanh (in-memory)
- ✅ Phù hợp cho dev/testing

**Cons:**
- ❌ Messages mất khi restart
- ❌ Không scale được (chỉ 1 server)
- ❌ Không persistent

---

### External Broker (Redis, RabbitMQ, ActiveMQ)

**Configuration (Redis example):**

```java
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Enable external broker (Redis)
    registry.enableStompBrokerRelay("/topic", "/queue")
            .setRelayHost("localhost")
            .setRelayPort(61613)
            .setClientLogin("guest")
            .setClientPasscode("guest");
    
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
}
```

**Characteristics:**

| Feature | External Broker |
|---------|-----------------|
| **Type** | Standalone service |
| **Persistence** | ✅ Messages stored to disk |
| **Clustering** | ✅ Multiple servers supported |
| **Scalability** | ✅ Horizontal scaling |
| **Setup** | ⚠️ Requires installation |
| **Performance** | ✅ High throughput |
| **Use Case** | Production, large scale |

**Pros:**
- ✅ Persistent storage
- ✅ Clustering support (multiple backend servers)
- ✅ Load balancing
- ✅ High availability
- ✅ Advanced features (TTL, priorities, etc.)

**Cons:**
- ❌ More complex setup
- ❌ Requires external service
- ❌ Additional infrastructure

---

## 🔄 Message Flow với Message Broker

### Complete Flow

```
┌──────────────────────────────────────────────────────────┐
│ 1. Producer Sends Message                                │
│                                                           │
│  NotificationService:                                     │
│    webSocketService.sendNotificationToUser(123, msg)     │
│      ↓                                                    │
│  SimpMessagingTemplate:                                   │
│    convertAndSendToUser("123", "/queue/notifications")   │
└────────────────────┬─────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────┐
│ 2. Message Broker Receives                               │
│                                                           │
│  Input:                                                   │
│    - userId: "123"                                        │
│    - destination: "/queue/notifications"                 │
│    - payload: { id: 1, title: "New task", ... }         │
│                                                           │
│  Step 1: Expand destination                              │
│    /queue/notifications → /user/123/queue/notifications  │
│                                                           │
│  Step 2: Lookup subscriptions                            │
│    Find sessions subscribed to:                          │
│    /user/123/queue/notifications                         │
│                                                           │
│  Step 3: Found subscribers                               │
│    - Session ABC (User 123, Desktop)                     │
│    - Session XYZ (User 123, Mobile)                      │
└────────────────────┬─────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────┐
│ 3. Message Broker Routes                                 │
│                                                           │
│  Action: Send message to subscribers                     │
│                                                           │
│  To Session ABC:                                          │
│    STOMP MESSAGE frame                                    │
│    destination: /user/queue/notifications                │
│    content-type: application/json                        │
│    { id: 1, title: "New task", ... }                    │
│                                                           │
│  To Session XYZ:                                          │
│    STOMP MESSAGE frame                                    │
│    destination: /user/queue/notifications                │
│    content-type: application/json                        │
│    { id: 1, title: "New task", ... }                    │
└────────────────────┬─────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────┐
│ 4. WebSocket Transmits                                   │
│                                                           │
│  Session ABC: Send over WebSocket connection             │
│  Session XYZ: Send over WebSocket connection             │
└────────────────────┬─────────────────────────────────────┘
                     │
                     ↓
┌──────────────────────────────────────────────────────────┐
│ 5. Clients Receive                                       │
│                                                           │
│  Desktop browser:                                         │
│    Subscription callback executes                        │
│    UI updates with notification ✅                       │
│                                                           │
│  Mobile browser:                                          │
│    Subscription callback executes                        │
│    UI updates with notification ✅                       │
└──────────────────────────────────────────────────────────┘
```

---

## 💡 Why Message Broker is Important

### Without Message Broker

❌ **Producer phải biết tất cả consumers:**

```java
// Bad: Producer directly manages consumers
for (WebSocketSession session : sessions) {
    if (session.getUserId().equals("123")) {
        session.sendMessage(msg); // Manual routing
    }
}
```

**Problems:**
- Producer phải track tất cả sessions
- Tight coupling giữa producer và consumer
- Khó scale, khó maintain

---

### With Message Broker

✅ **Producer chỉ cần gửi đến broker:**

```java
// Good: Producer sends to broker, let it handle routing
simpMessagingTemplate.convertAndSendToUser("123", "/queue/notifications", msg);
```

**Benefits:**
- ✅ Decoupling: Producer không cần biết consumers
- ✅ Abstraction: Broker handles routing logic
- ✅ Scalability: Easy to add more consumers
- ✅ Flexibility: Change routing without changing code

---

## 🎯 Message Broker Responsibilities

### 1. **Subscription Registry**

Lưu trữ ai đang subscribe đến destination nào:

```
Subscription Table:
┌────────────┬──────────────────────────────┬───────────────┐
│ Session ID │ Destination                  │ User ID       │
├────────────┼──────────────────────────────┼───────────────┤
│ ABC        │ /user/123/queue/notifications│ 123           │
│ XYZ        │ /user/123/queue/notifications│ 123           │
│ PQR        │ /user/456/queue/notifications│ 456           │
│ ABC        │ /topic/announcements         │ 123           │
│ XYZ        │ /topic/announcements         │ 123           │
│ PQR        │ /topic/announcements         │ 456           │
└────────────┴──────────────────────────────┴───────────────┘
```

---

### 2. **Destination Matching**

Match incoming messages với subscriptions:

```java
// Incoming message
Destination: /user/123/queue/notifications

// Matching algorithm
for (Subscription sub : subscriptions) {
    if (sub.matches("/user/123/queue/notifications")) {
        sendToSession(sub.sessionId, message);
    }
}

// Results
Matched:
  - Session ABC ✅
  - Session XYZ ✅
```

---

### 3. **Message Delivery**

Gửi messages đến đúng destinations:

```
Message Queue (Internal):
┌──────────────────────────────────────────┐
│ Pending Messages:                        │
│ 1. To Session ABC: { notification }     │
│ 2. To Session XYZ: { notification }     │
│ 3. To Session PQR: { count: 5 }        │
└──────────────────────────────────────────┘
         ↓
    Delivery
         ↓
┌──────────────────────────────────────────┐
│ Delivered:                               │
│ ✅ Session ABC                           │
│ ✅ Session XYZ                           │
│ ✅ Session PQR                           │
└──────────────────────────────────────────┘
```

---

### 4. **Session Lifecycle Management**

Track sessions từ connect đến disconnect:

```
Session ABC Lifecycle:

1. Connect
   ↓
   Broker creates session entry
   
2. Subscribe to /user/123/queue/notifications
   ↓
   Broker adds subscription
   
3. Receive messages
   ↓
   Broker routes messages to session
   
4. Disconnect
   ↓
   Broker removes session and subscriptions ✅
```

---

## 🔧 Configuration Examples

### SimpleBroker Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable SimpleBroker (in-memory)
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Prefix for messages FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");
        
        // Prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

**What this does:**

```
SimpleBroker handles:
  ✅ /topic/* destinations (broadcast)
  ✅ /queue/* destinations (point-to-point)
  ✅ /user/* destinations (user-specific)

Application handles:
  ✅ /app/* destinations (messages from client)
```

---

### External Broker Configuration (RabbitMQ)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable RabbitMQ broker
        registry.enableStompBrokerRelay("/topic", "/queue", "/exchange")
                .setRelayHost("localhost")
                .setRelayPort(61613) // STOMP port
                .setClientLogin("guest")
                .setClientPasscode("guest")
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);
        
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

**Benefits:**

```
RabbitMQ provides:
  ✅ Persistent message storage
  ✅ Clustering support
  ✅ Load balancing across multiple servers
  ✅ Advanced routing features
  ✅ Message acknowledgment
  ✅ Dead letter queues
```

---

## 📊 Comparison Summary

| Aspect | SimpleBroker | External Broker |
|--------|--------------|-----------------|
| **Setup Complexity** | ✅ Very easy | ⚠️ Moderate |
| **Configuration** | Minimal | Requires broker installation |
| **Persistence** | ❌ None | ✅ Messages stored |
| **Scalability** | ❌ Single server | ✅ Multi-server clustering |
| **Performance** | ✅ Fast (in-memory) | ✅ High throughput |
| **Message Guarantee** | ❌ No guarantee | ✅ Delivery guarantees |
| **Load Balancing** | ❌ No | ✅ Yes |
| **Monitoring** | ⚠️ Limited | ✅ Advanced tools |
| **Cost** | ✅ Free (built-in) | ⚠️ Infrastructure cost |
| **Use Case** | Dev/Testing | Production |

---

## 🎯 Summary

### Message Broker Là Gì?

**Message Broker** = Middleware component route messages từ producers đến consumers

### Vai Trò Chính:

1. ✅ **Message Routing** - Route messages đến đúng destinations
2. ✅ **Subscription Management** - Track ai subscribe đến đâu
3. ✅ **Destination Patterns** - Hỗ trợ broadcast, point-to-point, user-specific
4. ✅ **Message Storage** - (Optional) Lưu trữ messages
5. ✅ **Session Management** - Track active sessions

### Trong WebSocket Project:

```
Producers (Backend Services)
  ↓ Send messages
Message Broker (SimpleBroker hoặc RabbitMQ)
  ↓ Route based on destinations
  ↓ Match with subscriptions
Consumers (WebSocket Sessions)
  ↓ Receive messages
  ↓ Update UI
```

### Key Benefit:

**Decoupling** - Producer không cần biết consumers, Broker handles routing! ✅

---

## 📚 Related Documentation

- **WEBSOCKET_CONFIG_EXPLAINED.md** - Broker configuration details
- **SESSION_ROUTING_EXPLAINED.md** - How routing works
- **WEBSOCKET_SERVICE_EXPLAINED.md** - Using SimpMessagingTemplate
- **INDEX.md** - Complete documentation index

---

**Last Updated**: 2025-11-17
