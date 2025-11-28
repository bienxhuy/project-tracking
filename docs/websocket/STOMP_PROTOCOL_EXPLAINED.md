# STOMP Protocol - Giải Thích Chi Tiết

## 🎯 STOMP Là Gì?

**STOMP** = **S**imple **T**ext **O**riented **M**essaging **P**rotocol

**Definition:**
- Là một **text-based protocol** để giao tiếp với message brokers
- Thiết kế đơn giản, dễ implement
- Hoạt động trên **WebSocket** (hoặc TCP)
- Giống như **HTTP** nhưng cho messaging

---

## 📖 STOMP vs WebSocket vs HTTP

### Comparison

| Protocol | Type | Purpose | Format |
|----------|------|---------|--------|
| **HTTP** | Request/Response | Web requests | Text-based |
| **WebSocket** | Full-duplex | Real-time bidirectional | Binary/Text |
| **STOMP** | Messaging | Message routing | Text-based |

### Relationship

```
┌─────────────────────────────────────────┐
│           Application Layer             │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │     STOMP Protocol              │   │
│  │  - CONNECT, SUBSCRIBE, SEND     │   │
│  │  - Text frames                  │   │
│  │  - Destination routing          │   │
│  └──────────────┬──────────────────┘   │
│                 │                        │
│                 ↓                        │
│  ┌─────────────────────────────────┐   │
│  │     WebSocket Protocol          │   │
│  │  - Persistent connection        │   │
│  │  - Full-duplex                  │   │
│  │  - Low latency                  │   │
│  └──────────────┬──────────────────┘   │
│                 │                        │
│                 ↓                        │
│  ┌─────────────────────────────────┐   │
│  │     TCP/IP                      │   │
│  └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
```

**Analogy:**
- **WebSocket** = Đường ống (pipe) kết nối 2 bên
- **STOMP** = Ngôn ngữ (language) để nói chuyện qua đường ống đó
- **Message Broker** = Người phân phối (router) tin nhắn

---

## 🔤 STOMP Frame Format

STOMP sử dụng **text-based frames** giống như HTTP requests.

### Frame Structure

```
COMMAND
header1:value1
header2:value2

Body^@
```

**Components:**

1. **COMMAND** - Action to perform (CONNECT, SEND, SUBSCRIBE, etc.)
2. **Headers** - Key-value pairs (như HTTP headers)
3. **Empty line** - Separator
4. **Body** - Message payload
5. **NULL byte (^@)** - Frame terminator

---

## 📋 STOMP Commands (Client → Server)

### 1. CONNECT - Establish Connection

**Purpose:** Authenticate và establish session với server

**Frame:**
```
CONNECT
Authorization:Bearer eyJhbGc...
accept-version:1.2
host:localhost

^@
```

**Headers:**
- `Authorization` - JWT token for authentication
- `accept-version` - STOMP version (1.0, 1.1, 1.2)
- `host` - Virtual host name

**In Code:**
```typescript
// Frontend: @stomp/stompjs automatically sends CONNECT
this.client = new Client({
  connectHeaders: {
    Authorization: `Bearer ${token}`,
  }
});
this.client.activate();
```

**Server Response:**
```
CONNECTED
version:1.2
session:abc-123-def

^@
```

---

### 2. SUBSCRIBE - Register for Messages

**Purpose:** Subscribe to a destination để receive messages

**Frame:**
```
SUBSCRIBE
id:sub-0
destination:/user/queue/notifications
ack:auto

^@
```

**Headers:**
- `id` - Unique subscription ID
- `destination` - Destination path (like URL)
- `ack` - Acknowledgment mode (auto, client, client-individual)

**In Code:**
```typescript
// Frontend
this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
  const notification = JSON.parse(message.body);
  console.log('Received:', notification);
});
```

**Generates Frame:**
```
SUBSCRIBE
id:sub-0
destination:/user/queue/notifications
ack:auto

^@
```

---

### 3. SEND - Send Message to Server

**Purpose:** Send message to a destination on server

**Frame:**
```
SEND
destination:/app/sendMessage
content-type:application/json

{"text":"Hello World"}^@
```

**Headers:**
- `destination` - Destination path (prefixed with /app)
- `content-type` - MIME type of body

**In Code:**
```typescript
// Frontend
webSocketService.send('/app/sendMessage', { text: 'Hello World' });

// Implementation
this.client.publish({
  destination: '/app/sendMessage',
  body: JSON.stringify({ text: 'Hello World' }),
});
```

**Use Case:**
- Bidirectional communication
- Client sends messages to server
- Server can process and respond

---

### 4. UNSUBSCRIBE - Cancel Subscription

**Purpose:** Stop receiving messages from a destination

**Frame:**
```
UNSUBSCRIBE
id:sub-0

^@
```

**In Code:**
```typescript
// Frontend
const subscription = this.client.subscribe('/user/queue/notifications', callback);

// Later, unsubscribe
subscription.unsubscribe();
```

---

### 5. DISCONNECT - Close Connection

**Purpose:** Gracefully disconnect from server

**Frame:**
```
DISCONNECT
receipt:77

^@
```

**In Code:**
```typescript
// Frontend
this.client.deactivate();
```

---

## 📤 STOMP Frames (Server → Client)

### 1. MESSAGE - Deliver Message

**Purpose:** Server sends message to subscribed client

**Frame:**
```
MESSAGE
destination:/user/queue/notifications
message-id:007
subscription:sub-0
content-type:application/json

{"id":1,"title":"New Task","message":"You have been assigned"}^@
```

**Headers:**
- `destination` - Where message came from
- `message-id` - Unique message ID
- `subscription` - Subscription ID that matched
- `content-type` - MIME type

**In Code (Receiving):**
```typescript
// Frontend subscription callback receives MESSAGE frame
this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
  // message.body contains the JSON payload
  const notification = JSON.parse(message.body);
});
```

---

### 2. RECEIPT - Acknowledge Command

**Purpose:** Confirm that server processed a command

**Frame:**
```
RECEIPT
receipt-id:77

^@
```

---

### 3. ERROR - Report Error

**Purpose:** Notify client of an error

**Frame:**
```
ERROR
message:Access denied

You are not authorized^@
```

**In Code:**
```typescript
// Frontend handles errors
this.client = new Client({
  onStompError: (frame) => {
    console.error('STOMP error:', frame.headers.message);
    console.error('Details:', frame.body);
  }
});
```

---

## 🔄 Complete STOMP Flow

### Connection & Subscription Flow

```
┌─────────────┐                              ┌─────────────┐
│   Client    │                              │   Server    │
│  (Browser)  │                              │  (Spring)   │
└──────┬──────┘                              └──────┬──────┘
       │                                            │
       │  1. WebSocket Handshake                   │
       │───────────────────────────────────────────>│
       │  HTTP Upgrade: websocket                  │
       │                                            │
       │  2. HTTP 101 Switching Protocols          │
       │<───────────────────────────────────────────│
       │  WebSocket connection established ✅       │
       │                                            │
       │  3. STOMP CONNECT Frame                   │
       │───────────────────────────────────────────>│
       │  CONNECT                                  │
       │  Authorization: Bearer xxx                │
       │                                            │
       │                                            │ Extract JWT
       │                                            │ Validate token
       │                                            │ Create Principal
       │                                            │ Create session
       │                                            │
       │  4. STOMP CONNECTED Frame                 │
       │<───────────────────────────────────────────│
       │  CONNECTED                                │
       │  session: abc-123                         │
       │                                            │
       │  5. STOMP SUBSCRIBE Frame                 │
       │───────────────────────────────────────────>│
       │  SUBSCRIBE                                │
       │  id: sub-0                                │
       │  destination: /user/queue/notifications   │
       │                                            │
       │                                            │ Store subscription
       │                                            │ Map to session
       │                                            │
       │  6. Ready to receive messages ✅           │
       │                                            │
```

---

### Message Delivery Flow

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Backend   │     │    Message   │     │   Client    │
│   Service   │     │    Broker    │     │  (Browser)  │
└──────┬──────┘     └──────┬───────┘     └──────┬──────┘
       │                   │                    │
       │ Send notification │                    │
       │ to user 123       │                    │
       │──────────────────>│                    │
       │                   │                    │
       │                   │ Lookup sessions    │
       │                   │ for user 123       │
       │                   │                    │
       │                   │ STOMP MESSAGE      │
       │                   │ Frame              │
       │                   │───────────────────>│
       │                   │ MESSAGE            │
       │                   │ destination: /user/│
       │                   │   queue/notifications
       │                   │ body: {"id":1,...} │
       │                   │                    │
       │                   │                    │ Parse JSON
       │                   │                    │ Trigger callback
       │                   │                    │ Update UI ✅
       │                   │                    │
```

---

## 🎯 STOMP Destinations

### Destination Format

Destinations are like **URLs** but for messaging:

```
/topic/announcements        → Broadcast to all
/queue/tasks               → Point-to-point
/user/queue/notifications  → User-specific
/app/sendMessage           → Application endpoint
```

### Destination Prefixes

| Prefix | Handled By | Purpose | Example |
|--------|------------|---------|---------|
| `/topic` | Message Broker | Broadcast | `/topic/announcements` |
| `/queue` | Message Broker | Point-to-point | `/queue/tasks` |
| `/user` | Message Broker | User-specific | `/user/queue/notifications` |
| `/app` | Application | Controller methods | `/app/chat` |

---

### Example 1: Subscribe to User-Specific Notifications

**Client:**
```typescript
client.subscribe('/user/queue/notifications', (message) => {
  console.log('Notification:', JSON.parse(message.body));
});
```

**STOMP Frame:**
```
SUBSCRIBE
id:sub-0
destination:/user/queue/notifications

^@
```

**Spring expands to:**
```
/user/123/queue/notifications  (where 123 = principal/userId)
```

**Backend sends:**
```java
simpMessagingTemplate.convertAndSendToUser("123", "/queue/notifications", msg);
```

**Results in STOMP MESSAGE frame to client:**
```
MESSAGE
destination:/user/queue/notifications
subscription:sub-0
message-id:007
content-type:application/json

{"id":1,"title":"New Task"}^@
```

---

### Example 2: Broadcast to All Users

**Client:**
```typescript
client.subscribe('/topic/announcements', (message) => {
  console.log('Announcement:', JSON.parse(message.body));
});
```

**Backend sends:**
```java
simpMessagingTemplate.convertAndSend("/topic/announcements", "Server maintenance");
```

**All subscribed clients receive:**
```
MESSAGE
destination:/topic/announcements
subscription:sub-1

Server maintenance^@
```

---

## 🔐 STOMP Headers

### Common Headers

| Header | Used In | Purpose | Example |
|--------|---------|---------|---------|
| `destination` | SEND, SUBSCRIBE, MESSAGE | Route messages | `/user/queue/notifications` |
| `id` | SUBSCRIBE, UNSUBSCRIBE | Subscription ID | `sub-0` |
| `ack` | SUBSCRIBE | Acknowledgment mode | `auto`, `client` |
| `message-id` | MESSAGE | Unique message ID | `007` |
| `subscription` | MESSAGE | Which subscription matched | `sub-0` |
| `content-type` | SEND, MESSAGE | MIME type | `application/json` |
| `Authorization` | CONNECT | JWT token | `Bearer xxx` |
| `receipt` | Any | Request receipt | `receipt-id-123` |

---

### Custom Headers

You can add custom headers:

**Client sends:**
```typescript
client.publish({
  destination: '/app/chat',
  body: 'Hello',
  headers: {
    priority: 'high',
    'custom-header': 'value'
  }
});
```

**STOMP Frame:**
```
SEND
destination:/app/chat
priority:high
custom-header:value

Hello^@
```

---

## 💡 STOMP in Your Project

### Frontend (websocket.service.ts)

```typescript
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Create STOMP client
this.client = new Client({
  // WebSocket factory (uses SockJS for fallback)
  webSocketFactory: () => new SockJS('http://localhost:9090/ws'),
  
  // CONNECT frame headers
  connectHeaders: {
    Authorization: `Bearer ${token}`,
  },

  // Callback when CONNECTED frame received
  onConnect: () => {
    console.log('STOMP connected');
    this.subscribeToTopics(userId);
  },

  // Callback when ERROR frame received
  onStompError: (frame) => {
    console.error('STOMP error:', frame);
  },
});

// Send CONNECT frame
this.client.activate();
```

---

### Subscription Example

```typescript
// Send SUBSCRIBE frame
this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
  // Receives MESSAGE frames
  // message.body = JSON string
  // message.headers = STOMP headers
  
  const notification = JSON.parse(message.body);
  console.log('Received notification:', notification);
});
```

**Generated STOMP Frame:**
```
SUBSCRIBE
id:sub-0
destination:/user/queue/notifications
ack:auto

^@
```

---

### Backend (Spring Boot)

```java
@Configuration
@EnableWebSocketMessageBroker  // Enable STOMP over WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Message broker handles /topic and /queue
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Application handles /app
        registry.setApplicationDestinationPrefixes("/app");
        
        // User-specific prefix
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint for STOMP
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

---

## 🔄 STOMP Message Flow in Project

### Complete Flow

```
1. Client Connects
   ↓
   STOMP CONNECT frame sent
   ↓
   Spring validates JWT in Authorization header
   ↓
   STOMP CONNECTED frame returned
   ✅ Connection established

2. Client Subscribes
   ↓
   STOMP SUBSCRIBE frame sent
   destination: /user/queue/notifications
   ↓
   Spring stores subscription in registry
   Internal destination: /user/123/queue/notifications
   ✅ Subscription active

3. Backend Sends Notification
   ↓
   simpMessagingTemplate.convertAndSendToUser("123", "/queue/notifications", msg)
   ↓
   Message Broker expands: /user/123/queue/notifications
   ↓
   Broker finds matching subscriptions
   ↓
   STOMP MESSAGE frame sent to client
   ↓
   Client receives in subscription callback
   ✅ Notification delivered

4. Client Disconnects
   ↓
   STOMP DISCONNECT frame sent
   ↓
   Spring removes subscriptions and session
   ✅ Clean disconnect
```

---

## 🎯 Why Use STOMP?

### Advantages

1. **Simple Text Format**
   - Easy to read and debug
   - Human-readable frames
   - No complex binary parsing

2. **Destination Routing**
   - URL-like destinations
   - Clear message routing
   - Pattern matching support

3. **Subscription Model**
   - Publish-Subscribe pattern
   - Multiple subscribers
   - Selective message delivery

4. **Interoperability**
   - Language agnostic
   - Many client libraries
   - Standard protocol

5. **Built-in Features**
   - Acknowledgments
   - Transactions
   - Receipts
   - Error handling

---

### Comparison with Raw WebSocket

#### Raw WebSocket (Without STOMP)

```typescript
const ws = new WebSocket('ws://localhost:9090/ws');

ws.onmessage = (event) => {
  // What is this message?
  // Who should handle it?
  // How to route it?
  const data = JSON.parse(event.data);
  
  // Manual routing logic needed
  if (data.type === 'notification') {
    handleNotification(data);
  } else if (data.type === 'count') {
    handleCount(data);
  }
};

// Send message
ws.send(JSON.stringify({ action: 'subscribe', channel: 'notifications' }));
```

**Problems:**
- ❌ No standard message format
- ❌ Manual routing logic
- ❌ No subscription management
- ❌ Custom protocol needed

---

#### STOMP over WebSocket (Clean)

```typescript
const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:9090/ws')
});

// Subscribe to specific destinations
client.subscribe('/user/queue/notifications', handleNotification);
client.subscribe('/user/queue/notification-count', handleCount);

client.activate();
```

**Benefits:**
- ✅ Standard protocol
- ✅ Automatic routing
- ✅ Built-in subscription management
- ✅ Clean API

---

## 📊 STOMP Frame Examples

### Example 1: Client Connects with JWT

**STOMP Frame:**
```
CONNECT
Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
accept-version:1.2
heart-beat:10000,10000

^@
```

**Server Response:**
```
CONNECTED
version:1.2
heart-beat:10000,10000
session:abc-123-def-456

^@
```

---

### Example 2: Subscribe to Notifications

**STOMP Frame:**
```
SUBSCRIBE
id:sub-0
destination:/user/queue/notifications
ack:auto

^@
```

---

### Example 3: Receive Notification

**STOMP Frame:**
```
MESSAGE
destination:/user/queue/notifications
content-type:application/json
subscription:sub-0
message-id:007

{"id":1,"title":"New Task","message":"You have been assigned","type":"TASK_ASSIGNED"}^@
```

---

### Example 4: Unsubscribe

**STOMP Frame:**
```
UNSUBSCRIBE
id:sub-0

^@
```

---

### Example 5: Disconnect

**STOMP Frame:**
```
DISCONNECT
receipt:77

^@
```

**Server Response:**
```
RECEIPT
receipt-id:77

^@
```

---

## 🔧 STOMP Configuration in Project

### Backend Configuration

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker handles STOMP destinations
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Application endpoints use /app prefix
        registry.setApplicationDestinationPrefixes("/app");
        
        // User-specific destinations use /user prefix
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP endpoint: /ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // SockJS fallback for STOMP
    }
}
```

---

### Frontend Configuration

```typescript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Create STOMP client
const client = new Client({
  // Use SockJS for WebSocket transport
  webSocketFactory: () => new SockJS('http://localhost:9090/ws'),
  
  // STOMP connection headers
  connectHeaders: {
    Authorization: `Bearer ${token}`,
  },
  
  // STOMP protocol settings
  reconnectDelay: 3000,        // Auto-reconnect delay
  heartbeatIncoming: 10000,    // Expect heartbeat every 10s
  heartbeatOutgoing: 10000,    // Send heartbeat every 10s
  
  // Debug STOMP frames (dev only)
  debug: (str) => console.log('STOMP:', str),
});

// Activate STOMP connection
client.activate();
```

---

## 📚 Summary

### STOMP Là Gì?

**STOMP** = Simple Text Oriented Messaging Protocol

- Text-based messaging protocol
- Works over WebSocket (or TCP)
- Provides message routing via destinations
- Subscription-based message delivery

---

### Key Concepts

1. **Frames** - Text-based messages (like HTTP)
2. **Commands** - CONNECT, SUBSCRIBE, SEND, etc.
3. **Destinations** - URL-like paths for routing
4. **Headers** - Key-value metadata
5. **Body** - Message payload (usually JSON)

---

### STOMP Commands

**Client → Server:**
- `CONNECT` - Establish connection
- `SUBSCRIBE` - Register for messages
- `SEND` - Send message to server
- `UNSUBSCRIBE` - Cancel subscription
- `DISCONNECT` - Close connection

**Server → Client:**
- `CONNECTED` - Connection established
- `MESSAGE` - Deliver message
- `RECEIPT` - Command acknowledged
- `ERROR` - Error occurred

---

### In Your Project

**Purpose:** STOMP provides structured messaging over WebSocket

**Flow:**
```
Client sends STOMP CONNECT
  ↓
Client sends STOMP SUBSCRIBE (/user/queue/notifications)
  ↓
Backend sends notification via Message Broker
  ↓
Broker sends STOMP MESSAGE to client
  ↓
Client receives in subscription callback ✅
```

**Benefits:**
- ✅ Standard protocol (not custom)
- ✅ Automatic routing by destination
- ✅ Subscription management
- ✅ Works with Message Broker
- ✅ Clean, simple API

---

## 📖 Related Documentation

- **MESSAGE_BROKER_EXPLAINED.md** - How STOMP works with Message Broker
- **SESSION_ROUTING_EXPLAINED.md** - How destinations are routed
- **WEBSOCKET_SERVICE_EXPLAINED.md** - @stomp/stompjs implementation
- **WEBSOCKET_CONFIG_EXPLAINED.md** - Spring STOMP configuration

---

**Last Updated**: 2025-11-17

**STOMP = The language that WebSocket clients and servers use to communicate in a structured way!** 🎯
