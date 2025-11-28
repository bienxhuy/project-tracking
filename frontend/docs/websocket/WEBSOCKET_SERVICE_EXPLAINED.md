# websocket.service.ts - Chi tiết giải thích

## 📁 File Location
```
frontend/src/services/websocket.service.ts
```

## 🎯 Mục đích
Service quản lý WebSocket connection sử dụng STOMP protocol over SockJS. Cung cấp API để connect, subscribe, và nhận messages real-time.

---

## 📝 Code với Giải thích chi tiết

### 1. Imports

```typescript
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
```

#### **`@stomp/stompjs`**
- **Package**: STOMP (Simple Text Oriented Messaging Protocol) client
- **Version**: 7.0.0
- **Purpose**: WebSocket messaging protocol

**STOMP Protocol:**
```
STOMP = Higher-level protocol built on top of WebSocket
WebSocket = Low-level TCP-like connection

STOMP provides:
  - Message routing
  - Subscriptions
  - Headers
  - Receipts
  - Transactions
```

**`Client` class:**
```typescript
class Client {
  activate(): void;           // Connect to server
  deactivate(): void;         // Disconnect
  subscribe(dest, callback);  // Subscribe to destination
  publish(params);           // Send message
}
```

**`IMessage` interface:**
```typescript
interface IMessage {
  command: string;      // STOMP command (MESSAGE, ERROR, etc.)
  headers: {[key: string]: string};
  body: string;         // Message payload (JSON string)
  ack(): void;         // Acknowledge message
  nack(): void;        // Negative acknowledge
}
```

#### **`sockjs-client`**
- **Package**: SockJS client library
- **Purpose**: WebSocket fallback/polyfill
- **Compatibility**: Works on old browsers

**SockJS benefits:**
```typescript
const socket = new SockJS('http://localhost:9090/ws');
// Automatically tries:
//  1. WebSocket (if supported)
//  2. XHR Streaming
//  3. XHR Polling
//  4. iFrame
//  ... và các fallbacks khác
```

---

### 2. Type Definitions

```typescript
export interface WebSocketNotification {
  id: number;
  title: string;
  message: string;
  type: string;
  referenceId?: number;
  referenceType?: string;
  triggeredById?: number;
  triggeredByName?: string;
  timestamp: string;
  isRead: boolean;
  action: string;
}
```

#### **TypeScript Interface Benefits:**

**Type Safety:**
```typescript
// ✅ Type-safe
const notification: WebSocketNotification = {
  id: 123,
  title: "New Task",
  message: "...",
  // ... TypeScript validates all required fields
};

// ❌ Type error
const invalid: WebSocketNotification = {
  id: "123",  // Error: Type 'string' not assignable to 'number'
};
```

**Optional Fields:**
```typescript
referenceId?: number;
// "?" = optional field
// Can be: number | undefined
```

**IDE Autocomplete:**
```typescript
notification.  // IDE shows: id, title, message, type, etc.
```

#### **Callback Type Definitions:**

```typescript
type NotificationCallback = (notification: WebSocketNotification) => void;
type NotificationCountCallback = (count: number) => void;
type ConnectionCallback = (connected: boolean) => void;
```

**Type Aliases:**
```typescript
type NotificationCallback = (notification: WebSocketNotification) => void;

// Equivalent to:
type NotificationCallback = {
  (notification: WebSocketNotification): void;
}

// Usage:
const callback: NotificationCallback = (notif) => {
  console.log(notif.title);
};
```

---

### 3. Class Declaration

```typescript
class WebSocketService {
  private client: Client | null = null;
  private isConnected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  
  private notificationCallbacks: NotificationCallback[] = [];
  private notificationCountCallbacks: NotificationCountCallback[] = [];
  private connectionCallbacks: ConnectionCallback[] = [];
```

#### **`private` keyword:**
```typescript
private client: Client | null = null;
// private = only accessible within this class
// Outside code CANNOT do: service.client
```

**Access modifiers:**
```typescript
public  methodName() {}  // Accessible anywhere
private methodName() {}  // Only within class
protected methodName() {} // Within class + subclasses
```

#### **Union Types:**
```typescript
private client: Client | null = null;
//               ^^^^^^   ^^^^
//               Type 1   Type 2
// Can be: Client OR null
```

**Why `| null`?**
```typescript
// Before connection
this.client = null;  // ✅ Valid

// After connection
this.client = new Client(...);  // ✅ Valid

// Type guard needed for usage
if (this.client) {
  this.client.activate();  // TypeScript knows it's not null here
}
```

#### **Callback Arrays:**
```typescript
private notificationCallbacks: NotificationCallback[] = [];
```

**Observer Pattern:**
```typescript
// Multiple components can listen
notificationCallbacks = [
  (notif) => console.log(notif),      // Component 1
  (notif) => showToast(notif),        // Component 2
  (notif) => updateBadge(notif),      // Component 3
];

// Notify all
this.notificationCallbacks.forEach(callback => {
  callback(notification);
});
```

---

### 4. Connect Method

```typescript
connect(userId: number, token?: string) {
  if (this.isConnected) {
    console.log('WebSocket already connected');
    return;
  }

  const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:9090/ws';
  
  console.log('Connecting to WebSocket:', wsUrl);

  this.client = new Client({
    // ... configuration
  });

  this.client.activate();
}
```

#### **Method Parameters:**

```typescript
connect(userId: number, token?: string)
//      ^^^^^^          ^^^^^
//      Required        Optional
```

**Optional parameter:**
```typescript
token?: string
// "?" = optional
// Can call: connect(123) or connect(123, "jwt-token")
```

**Default parameter:**
```typescript
connect(userId: number, token: string = "") {
  // token will be "" if not provided
}
```

#### **Guard Clause Pattern:**

```typescript
if (this.isConnected) {
  console.log('WebSocket already connected');
  return;  // Exit early
}
// Continue with connection logic...
```

**Without guard clause:**
```typescript
if (!this.isConnected) {
  // All connection logic nested here
  // Harder to read
}
```

#### **Environment Variables:**

```typescript
const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:9090/ws';
//            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//            Vite environment variable
```

**Vite env variables:**
```env
# .env file
VITE_WS_URL=http://localhost:9090/ws
```

**Access in code:**
```typescript
import.meta.env.VITE_WS_URL        // String | undefined
import.meta.env.VITE_API_BASE_URL
import.meta.env.DEV                // boolean
import.meta.env.PROD               // boolean
```

**Fallback pattern:**
```typescript
value || defaultValue
// If value is falsy (null, undefined, "", 0, false)
// Use defaultValue
```

---

### 5. Client Configuration

```typescript
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
  // ... callbacks
});
```

#### **`webSocketFactory`:**

```typescript
webSocketFactory: () => new SockJS(wsUrl)
```

**What it does:**
```typescript
// STOMP Client asks: "Give me a WebSocket"
// We return: new SockJS(wsUrl)
// SockJS handles fallbacks automatically
```

**Factory function pattern:**
```typescript
// NOT this (immediate execution):
webSocket: new SockJS(wsUrl)

// THIS (lazy execution):
webSocketFactory: () => new SockJS(wsUrl)
// Called only when needed
```

**SockJS initialization:**
```typescript
const socket = new SockJS('http://localhost:9090/ws');
// 1. Try WebSocket connection
// 2. If fails, try XHR Streaming
// 3. If fails, try XHR Polling
// 4. ...
```

#### **`connectHeaders`:**

```typescript
connectHeaders: token ? {
  Authorization: `Bearer ${token}`,
} : {},
```

**Ternary operator:**
```typescript
condition ? valueIfTrue : valueIfFalse

// Expanded:
let headers;
if (token) {
  headers = { Authorization: `Bearer ${token}` };
} else {
  headers = {};
}
```

**Authorization header:**
```typescript
Authorization: `Bearer ${token}`
// Template literal: `Bearer ${token}`
// Result: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**STOMP CONNECT frame:**
```
CONNECT
Authorization: Bearer eyJhbGc...
accept-version:1.2
heart-beat:10000,10000

^@
```

#### **`debug` callback:**

```typescript
debug: (str) => {
  if (import.meta.env.DEV) {
    console.log('[WebSocket Debug]', str);
  }
},
```

**Arrow function:**
```typescript
(str) => { ... }
// Equivalent to:
function(str) { ... }
```

**Development only logging:**
```typescript
if (import.meta.env.DEV) {
  // Only log in development
  // Not in production (keeps console clean)
}
```

**Debug output examples:**
```
[WebSocket Debug] STOMP: Received frame
[WebSocket Debug] STOMP: Subscribe /user/queue/notifications
[WebSocket Debug] STOMP: Heartbeat outgoing
```

#### **`reconnectDelay`:**

```typescript
reconnectDelay: this.reconnectDelay,  // 3000ms
```

**Auto-reconnect:**
```
Connection lost
  ↓
Wait 3000ms
  ↓
Try reconnect
  ↓
If fails, wait 3000ms
  ↓
Try again
  ...
```

#### **Heartbeats:**

```typescript
heartbeatIncoming: 10000,  // Expect heartbeat from server every 10s
heartbeatOutgoing: 10000,  // Send heartbeat to server every 10s
```

**Purpose:**
- Detect dead connections
- Keep connection alive
- NAT/Firewall traversal

**Heartbeat flow:**
```
Client ──────> Server  (every 10s)
  ↑              │
  │              ↓
  └────────── Client  (every 10s)

If no heartbeat received:
  → Assume connection dead
  → Trigger reconnect
```

**STOMP heartbeat frames:**
```
\n  (newline character)
```

**Heartbeat configuration matrix:**
```typescript
heartbeatIncoming: 0     // Don't expect heartbeats
heartbeatOutgoing: 0     // Don't send heartbeats
heartbeatIncoming: 10000 // Expect every 10s
heartbeatOutgoing: 5000  // Send every 5s
```

---

### 6. Connection Callbacks

```typescript
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
```

#### **`onConnect` callback:**

**Execution timing:**
```
client.activate()
  ↓
SockJS connects
  ↓
STOMP handshake
  ↓
CONNECTED frame received
  ↓
onConnect() called ✅
```

**State updates:**
```typescript
this.isConnected = true;          // Update connection status
this.reconnectAttempts = 0;       // Reset reconnect counter
this.notifyConnectionCallbacks(true);  // Notify listeners
this.subscribeToTopics(userId);   // Subscribe to channels
```

**Why reset `reconnectAttempts`?**
```typescript
// Scenario: Network unstable
Attempt 1: Connect → Fail → reconnectAttempts = 1
Attempt 2: Connect → Fail → reconnectAttempts = 2
Attempt 3: Connect → SUCCESS → reconnectAttempts = 0 (reset!)
// Next disconnect starts fresh
```

#### **Error callbacks:**

**`onStompError` vs `onWebSocketError`:**

```typescript
onStompError      // Protocol-level errors (STOMP)
onWebSocketError  // Transport-level errors (WebSocket/SockJS)
```

**Examples:**

**STOMP Error:**
```
ERROR
message: Authentication failed
content-type: text/plain

Invalid credentials
^@
```

**WebSocket Error:**
```javascript
{
  type: "error",
  target: WebSocket,
  // Network error, connection refused, etc.
}
```

#### **`onDisconnect` callback:**

**Triggers:**
```
- Server shutdown
- Network interruption
- Client calls disconnect()
- Connection timeout
```

**Auto-reconnect:**
```typescript
this.handleReconnect(userId, token);
```

---

### 7. Subscribe to Topics

```typescript
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

  // Subscribe to broadcast notifications (optional)
  this.client.subscribe(`/topic/notifications`, (message: IMessage) => {
    const notification: WebSocketNotification = JSON.parse(message.body);
    console.log('Received broadcast notification:', notification);
    this.notifyNotificationCallbacks(notification);
  });

  console.log(`Subscribed to notification topics for user ${userId}`);
}
```

#### **Type Guard:**

```typescript
if (!this.client) return;
```

**TypeScript type narrowing:**
```typescript
// Before guard
this.client: Client | null

// After guard (in if block)
this.client: Client  // TypeScript knows it's not null
```

#### **`subscribe()` method:**

**Method signature:**
```typescript
subscribe(
  destination: string,
  callback: (message: IMessage) => void
): StompSubscription
```

**Multiple subscriptions:**
```typescript
subscription1 = client.subscribe('/user/queue/notifications', ...)
subscription2 = client.subscribe('/user/queue/notification-count', ...)
subscription3 = client.subscribe('/topic/notifications', ...)
// All active simultaneously
```

**Unsubscribe:**
```typescript
const subscription = client.subscribe('/user/queue/notifications', ...);

// Later
subscription.unsubscribe();
```

#### **Message Parsing:**

```typescript
const notification: WebSocketNotification = JSON.parse(message.body);
```

**`message.body` is JSON string:**
```typescript
message.body = '{"id":123,"title":"New Task","message":"..."}'
// typeof message.body === "string"
```

**After JSON.parse:**
```typescript
notification = {
  id: 123,
  title: "New Task",
  message: "..."
}
// typeof notification === "object"
```

**Type annotation:**
```typescript
const notification: WebSocketNotification = JSON.parse(message.body);
// TypeScript validates structure
```

**Error handling:**
```typescript
try {
  const notification = JSON.parse(message.body);
} catch (error) {
  console.error('Invalid JSON:', message.body);
}
```

#### **Callback Notification:**

```typescript
this.notifyNotificationCallbacks(notification);
```

**Implementation:**
```typescript
private notifyNotificationCallbacks(notification: WebSocketNotification) {
  this.notificationCallbacks.forEach(callback => {
    try {
      callback(notification);  // Call each registered callback
    } catch (error) {
      console.error('Error in notification callback:', error);
    }
  });
}
```

**Flow:**
```
Server sends message
  ↓
WebSocket receives
  ↓
STOMP routes to subscription
  ↓
Callback executes: (message) => { ... }
  ↓
Parse JSON
  ↓
Notify all registered callbacks
  ↓
Component 1 updates UI
Component 2 shows toast
Component 3 plays sound
```

---

### 8. Reconnection Logic

```typescript
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
```

#### **Exponential Backoff:**

```typescript
this.reconnectDelay * this.reconnectAttempts
```

**Delay progression:**
```
Attempt 1: 3000ms * 1 = 3 seconds
Attempt 2: 3000ms * 2 = 6 seconds
Attempt 3: 3000ms * 3 = 9 seconds
Attempt 4: 3000ms * 4 = 12 seconds
Attempt 5: 3000ms * 5 = 15 seconds
Max attempts reached → Stop
```

**Why exponential backoff?**
- ✅ Reduces server load
- ✅ Gives server time to recover
- ✅ Prevents thundering herd

**Alternative strategies:**

**Fixed delay:**
```typescript
setTimeout(() => this.connect(), 3000);  // Always 3s
```

**True exponential:**
```typescript
setTimeout(() => this.connect(), Math.pow(2, this.reconnectAttempts) * 1000);
// 2s, 4s, 8s, 16s, 32s, 64s...
```

**With jitter:**
```typescript
const jitter = Math.random() * 1000;  // 0-1000ms
setTimeout(() => this.connect(), baseDelay + jitter);
// Prevents synchronized reconnects
```

#### **`setTimeout` closure:**

```typescript
setTimeout(() => {
  this.connect(userId, token);
}, delay);
```

**Captures variables:**
```typescript
// userId and token are captured in closure
// Values are preserved even after handleReconnect() returns
```

**Arrow function preserves `this`:**
```typescript
setTimeout(() => {
  this.connect();  // 'this' refers to WebSocketService
}, delay);

// vs

setTimeout(function() {
  this.connect();  // 'this' is undefined or window
}, delay);
```

---

### 9. Callback Registration

```typescript
onNotification(callback: NotificationCallback) {
  this.notificationCallbacks.push(callback);
  
  // Return unsubscribe function
  return () => {
    this.notificationCallbacks = this.notificationCallbacks.filter(cb => cb !== callback);
  };
}
```

#### **Observer Pattern:**

**Register:**
```typescript
const unsubscribe = webSocketService.onNotification((notification) => {
  console.log('New notification:', notification);
});
```

**Unsubscribe:**
```typescript
unsubscribe();  // Remove callback
```

#### **Return function pattern:**

```typescript
return () => {
  // Cleanup logic
  this.notificationCallbacks = this.notificationCallbacks.filter(cb => cb !== callback);
};
```

**Usage in React:**
```typescript
useEffect(() => {
  const unsubscribe = webSocketService.onNotification(handleNotification);
  
  return () => {
    unsubscribe();  // Cleanup on unmount
  };
}, []);
```

#### **Array `filter` method:**

```typescript
this.notificationCallbacks.filter(cb => cb !== callback)
```

**Example:**
```typescript
callbacks = [fn1, fn2, fn3]

// Remove fn2
callbacks = callbacks.filter(cb => cb !== fn2)
// Result: [fn1, fn3]
```

**Reference equality:**
```typescript
const fn1 = () => console.log('A');
const fn2 = () => console.log('A');

fn1 !== fn2  // true (different function instances)
```

---

### 10. Singleton Pattern

```typescript
// Export singleton instance
export const webSocketService = new WebSocketService();
```

**Singleton benefits:**
```typescript
// One shared instance across app
import { webSocketService } from './websocket.service';

// Component 1
webSocketService.connect(userId);

// Component 2 (same instance)
webSocketService.onNotification(...);
```

**Without singleton:**
```typescript
// Each import creates new instance ❌
import { WebSocketService } from './websocket.service';

const service1 = new WebSocketService();  // Instance 1
const service2 = new WebSocketService();  // Instance 2
// Multiple connections, state not shared
```

---

## 🔄 Complete Message Flow

```
Backend sends notification
  ↓
WebSocket server transmits
  ↓
SockJS receives (browser)
  ↓
STOMP Client processes
  ↓
Finds matching subscription:
  /user/queue/notifications
  ↓
Calls subscription callback:
  (message: IMessage) => { ... }
  ↓
Parse JSON:
  JSON.parse(message.body)
  ↓
Notify all registered callbacks:
  this.notificationCallbacks.forEach(...)
  ↓
React hook receives:
  useWebSocketNotification
  ↓
Update React state:
  setNotifications([...])
  ↓
Component re-renders
  ↓
UI shows new notification ✅
```

---

## 🧪 Testing

### Manual Test

```typescript
import { webSocketService } from './websocket.service';

// Connect
webSocketService.connect(123, 'jwt-token');

// Listen
webSocketService.onNotification((notification) => {
  console.log('Received:', notification);
});

webSocketService.onConnectionChange((connected) => {
  console.log('Connected:', connected);
});

// Disconnect
webSocketService.disconnect();
```

---

## 📊 Performance

### Connection pooling
```typescript
// Singleton ensures single connection
// Multiple components share one WebSocket
// Efficient resource usage
```

### Message batching
```typescript
// Multiple subscriptions on same connection
// No overhead for additional subscriptions
```

### Memory management
```typescript
// Unsubscribe functions prevent memory leaks
useEffect(() => {
  const unsub = webSocketService.onNotification(...);
  return unsub;  // Cleanup
}, []);
```

---

**Tóm tắt:**
- WebSocket service sử dụng STOMP over SockJS
- Singleton pattern cho shared connection
- Observer pattern cho callback management
- Auto-reconnect với exponential backoff
- Type-safe với TypeScript interfaces
