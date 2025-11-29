# WebSocket Session Routing - Giải Thích Chi Tiết

## 🎯 Câu Hỏi Quan Trọng

**"Backend và Client giao tiếp qua WebSocket bằng session đúng không?"**

**Trả lời: ĐÚNG!** ✅

Backend kiểm tra có **session** của userId cần gửi, rồi bắn message đến session(s) đó.

---

## 🔄 Complete Session Routing Flow

### Step 1: Client Connects

```typescript
// Frontend: Connect to WebSocket
const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:9090/ws'),
  connectHeaders: {
    Authorization: `Bearer ${token}` // JWT token có userId
  }
});

client.activate();
```

**What happens:**
```
1. Client initiates WebSocket handshake
   ↓
2. Sends STOMP CONNECT frame với Authorization header
   ↓
3. Backend receives connection request
```

---

### Step 2: Backend Authenticates & Creates Session

```java
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = 
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Extract JWT token
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    
                    // Validate and extract userId
                    String userId = extractUserIdFromToken(authToken); // e.g., "123"
                    
                    // Set userId as Principal (IMPORTANT!)
                    accessor.setUser(new StompPrincipal(userId));
                }
                
                return message;
            }
        });
    }
}
```

**What happens:**
```
1. Interceptor extracts JWT token
   ↓
2. Validates token
   ↓
3. Extracts userId = "123"
   ↓
4. Creates Principal with userId
   ↓
5. Associates Principal with WebSocket session
   ↓
6. Stores in SimpUserRegistry:
   Session: {
     sessionId: "abc-def-ghi",
     principal: "123",  // ← User ID stored here!
     subscriptions: []
   }
```

---

### Step 3: Client Subscribes

```typescript
// Frontend: Subscribe to personal queue
client.subscribe('/user/queue/notifications', (message) => {
  console.log('Received:', message.body);
});
```

**What happens on client side:**
```
Client sends STOMP SUBSCRIBE frame:
  destination: /user/queue/notifications
  id: sub-0
```

**What happens on backend:**
```
1. Spring receives SUBSCRIBE frame
   ↓
2. Gets session's Principal (userId = "123")
   ↓
3. Expands destination internally:
   /user/queue/notifications
   → /user/123/queue/notifications  // Adds userId!
   ↓
4. Stores subscription in session:
   Session: {
     sessionId: "abc-def-ghi",
     principal: "123",
     subscriptions: [
       {
         id: "sub-0",
         destination: "/user/123/queue/notifications"  // ← Internal destination
       }
     ]
   }
```

**Important:** Client subscribe to `/user/queue/notifications`, but Spring internally maps it to `/user/123/queue/notifications` based on session's Principal!

---

### Step 4: Backend Sends Message

```java
// Backend: Send notification to user 123
@Service
public class WebSocketNotificationService {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
        // Send to specific user
        simpMessagingTemplate.convertAndSendToUser(
            userId.toString(),  // "123"
            "/queue/notifications",  // Destination
            message
        );
    }
}
```

**What happens:**
```
1. convertAndSendToUser("123", "/queue/notifications", msg)
   ↓
2. Spring prefixes with "/user" + userId:
   /user/123/queue/notifications
   ↓
3. Spring looks up in SimpUserRegistry:
   "Find all sessions where principal = '123'"
   ↓
4. Found sessions: [
     { sessionId: "abc-def-ghi", principal: "123" },
     { sessionId: "xyz-uvw-rst", principal: "123" }  // User có 2 tabs open
   ]
   ↓
5. Check each session's subscriptions:
   Session abc-def-ghi subscribed to /user/123/queue/notifications? YES ✅
   Session xyz-uvw-rst subscribed to /user/123/queue/notifications? YES ✅
   ↓
6. Send message to both sessions! ✅
```

---

### Step 5: Client Receives Message

```typescript
// Frontend: Subscription callback executes
client.subscribe('/user/queue/notifications', (message) => {
  const notification = JSON.parse(message.body);
  console.log('Received notification:', notification);
  // Update UI...
});
```

**Complete Flow:**
```
Backend Service
  ↓ convertAndSendToUser("123", "/queue/notifications", msg)
Spring adds prefix
  ↓ /user/123/queue/notifications
SimpUserRegistry lookup
  ↓ Find sessions with principal = "123"
Found 2 sessions (2 browser tabs)
  ↓ Both subscribed to /user/123/queue/notifications
Message Broker
  ↓ Route to both sessions
WebSocket transmits
  ↓ Send over network
Client receives
  ↓ Both tabs get message
Callback executes
  ↓ UI updates ✅
```

---

## 🔍 Session Management Deep Dive

### SimpUserRegistry

Spring maintains a registry of all active WebSocket sessions:

```java
@Autowired
private SimpUserRegistry simpUserRegistry;

public void debugSessions() {
    Set<SimpUser> users = simpUserRegistry.getUsers();
    
    for (SimpUser user : users) {
        System.out.println("User: " + user.getName()); // Principal (userId)
        
        for (SimpSession session : user.getSessions()) {
            System.out.println("  Session: " + session.getId());
            
            for (SimpSubscription sub : session.getSubscriptions()) {
                System.out.println("    Subscription: " + sub.getDestination());
            }
        }
    }
}
```

**Example output:**
```
User: 123
  Session: abc-def-ghi
    Subscription: /user/123/queue/notifications
    Subscription: /user/123/queue/notification-count
  Session: xyz-uvw-rst
    Subscription: /user/123/queue/notifications
    Subscription: /user/123/queue/notification-count

User: 456
  Session: pqr-stu-vwx
    Subscription: /user/456/queue/notifications
```

---

### Session Lifecycle

```
1. Client Connects
   ↓
   Spring creates WebSocket session
   sessionId: "abc-def-ghi"
   principal: null (initially)

2. STOMP CONNECT with JWT
   ↓
   Interceptor extracts userId from token
   ↓
   Sets principal: "123"
   ↓
   Session now: {
     sessionId: "abc-def-ghi",
     principal: "123"
   }

3. Client Subscribes
   ↓
   Spring adds subscription to session
   ↓
   Session now: {
     sessionId: "abc-def-ghi",
     principal: "123",
     subscriptions: ["/user/123/queue/notifications"]
   }

4. Client Disconnects (close tab, network issue)
   ↓
   Spring removes session from registry
   ↓
   No more messages sent to this session ✅
```

---

## 💡 Key Insights

### 1. Client Destination vs Backend Destination

**Client subscribes to:**
```typescript
/user/queue/notifications  // Generic, no userId
```

**Spring internally expands to:**
```java
/user/123/queue/notifications  // Specific to session's principal
```

**Backend sends to:**
```java
convertAndSendToUser("123", "/queue/notifications", msg)
  → Spring prefixes: /user/123/queue/notifications
```

**They match! ✅**

---

### 2. Multi-Tab Support

Một user có thể có **nhiều sessions** (multiple browser tabs):

```
User 123 has 2 tabs open:
  Tab 1: Session abc-def-ghi
  Tab 2: Session xyz-uvw-rst

Backend sends:
  convertAndSendToUser("123", "/queue/notifications", msg)

Spring delivers to:
  Session abc-def-ghi ✅
  Session xyz-uvw-rst ✅

Both tabs receive notification! ✅
```

---

### 3. Automatic Cleanup

```
Tab 1 closed → WebSocket disconnect
  ↓
Spring removes session abc-def-ghi from registry
  ↓
Next message only goes to session xyz-uvw-rst ✅
```

---

## 🔐 Security & Session Management

### Principal Extraction

```java
private String extractUserIdFromToken(String authToken) {
    if (authToken != null && authToken.startsWith("Bearer ")) {
        String token = authToken.substring(7);
        
        // Decode JWT
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        
        // Extract userId
        return claims.getSubject(); // "123"
    }
    
    throw new IllegalArgumentException("Invalid token");
}
```

### StompPrincipal Implementation

```java
public class StompPrincipal implements Principal {
    private final String name;
    
    public StompPrincipal(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name; // Returns userId
    }
}
```

---

## 📊 Routing Examples

### Example 1: User-Specific Notification

```java
// Backend
simpMessagingTemplate.convertAndSendToUser("123", "/queue/notifications", msg);
```

```
Spring Routing:
  Input: userId="123", destination="/queue/notifications"
  ↓
  Prefix: /user/123/queue/notifications
  ↓
  Lookup: Find sessions with principal="123"
  ↓
  Found: [session-abc, session-xyz]
  ↓
  Deliver: Send to both sessions ✅
```

**Frontend receives:**
```typescript
// Both tabs receive
client.subscribe('/user/queue/notifications', (message) => {
  // message.body contains notification
});
```

---

### Example 2: Broadcast to All Users

```java
// Backend
simpMessagingTemplate.convertAndSend("/topic/announcements", msg);
```

```
Spring Routing:
  Input: destination="/topic/announcements"
  ↓
  No user prefix (broadcast destination)
  ↓
  Lookup: Find ALL sessions subscribed to /topic/announcements
  ↓
  Found: [session-1, session-2, session-3, ...]
  ↓
  Deliver: Send to ALL sessions ✅
```

**Frontend receives:**
```typescript
// All connected clients receive
client.subscribe('/topic/announcements', (message) => {
  // message.body contains announcement
});
```

---

### Example 3: Notification Count Update

```java
// Backend
simpMessagingTemplate.convertAndSendToUser("123", "/queue/notification-count", 5);
```

```
Spring Routing:
  Input: userId="123", destination="/queue/notification-count"
  ↓
  Prefix: /user/123/queue/notification-count
  ↓
  Lookup: Find sessions with principal="123"
  ↓
  Found: [session-abc]
  ↓
  Deliver: Send count=5 to session ✅
```

**Frontend receives:**
```typescript
client.subscribe('/user/queue/notification-count', (message) => {
  const count = parseInt(message.body);
  console.log('Unread count:', count); // 5
});
```

---

## 🧪 Testing Session Routing

### Debug Endpoint

```java
@RestController
@RequestMapping("/api/debug/websocket")
public class WebSocketDebugController {
    
    @Autowired
    private SimpUserRegistry userRegistry;
    
    @GetMapping("/sessions")
    public Map<String, Object> getSessions() {
        Set<SimpUser> users = userRegistry.getUsers();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", users.size());
        
        List<Map<String, Object>> userList = new ArrayList<>();
        for (SimpUser user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getName()); // Principal
            userInfo.put("sessionCount", user.getSessions().size());
            
            List<String> sessionIds = user.getSessions().stream()
                .map(SimpSession::getId)
                .collect(Collectors.toList());
            userInfo.put("sessions", sessionIds);
            
            userList.add(userInfo);
        }
        result.put("users", userList);
        
        return result;
    }
    
    @PostMapping("/send-test/{userId}")
    public void sendTest(@PathVariable String userId) {
        WebSocketNotificationMessage msg = WebSocketNotificationMessage.builder()
            .id(999L)
            .title("Test")
            .message("Test message")
            .type("INFO")
            .timestamp(LocalDateTime.now().toString())
            .build();
        
        simpMessagingTemplate.convertAndSendToUser(userId, "/queue/notifications", msg);
    }
}
```

**Test Flow:**

```bash
# 1. Check current sessions
curl http://localhost:9090/api/debug/websocket/sessions

Response:
{
  "totalUsers": 2,
  "users": [
    {
      "userId": "123",
      "sessionCount": 2,
      "sessions": ["abc-def-ghi", "xyz-uvw-rst"]
    },
    {
      "userId": "456",
      "sessionCount": 1,
      "sessions": ["pqr-stu-vwx"]
    }
  ]
}

# 2. Send test notification to user 123
curl -X POST http://localhost:9090/api/debug/websocket/send-test/123

# 3. Both sessions of user 123 should receive message ✅
```

---

## 🎯 Common Scenarios

### Scenario 1: User Login on 2 Devices

```
User 123 logs in on:
  - Desktop browser (Chrome)
  - Mobile browser (Safari)

Spring creates 2 sessions:
  Session 1: desktop-session-id, principal="123"
  Session 2: mobile-session-id, principal="123"

Backend sends notification:
  convertAndSendToUser("123", "/queue/notifications", msg)

Both devices receive notification! ✅
```

---

### Scenario 2: User Closes One Tab

```
User 123 has 2 tabs:
  Tab 1: session-abc
  Tab 2: session-xyz

User closes Tab 1:
  ↓
WebSocket disconnect
  ↓
Spring removes session-abc from registry

SimpUserRegistry now:
  User 123: [session-xyz]  // Only 1 session

Next notification only goes to Tab 2 ✅
```

---

### Scenario 3: Session Timeout

```
User 123 idle for long time:
  ↓
Heartbeat fails
  ↓
Spring considers session dead
  ↓
Auto-disconnect and remove from registry

Frontend auto-reconnect:
  ↓
Creates new session with new sessionId
  ↓
Re-subscribes to destinations
  ↓
Back online ✅
```

---

## 📝 Summary

### Key Points

1. **Session = Connection**
   - Each WebSocket connection = 1 session
   - Session has sessionId + Principal (userId)

2. **Principal = User Identity**
   - Extracted from JWT token
   - Used for user-specific routing
   - One user can have multiple sessions (multiple tabs)

3. **Routing Process**
   ```
   Backend: convertAndSendToUser("123", "/queue/notifications", msg)
     ↓
   Spring: /user/123/queue/notifications
     ↓
   Registry: Find sessions where principal="123"
     ↓
   Deliver: Send to all matching sessions ✅
   ```

4. **Client Subscription**
   ```
   Client: subscribe('/user/queue/notifications')
     ↓
   Spring: Internally maps to /user/{principal}/queue/notifications
     ↓
   Match: When message arrives at /user/123/queue/notifications
     ↓
   Deliver: If session's principal = "123" ✅
   ```

5. **Automatic Management**
   - Spring handles session creation/cleanup
   - No manual session management needed
   - Multi-tab support automatic
   - Disconnection cleanup automatic

---

**Vậy câu trả lời là: ĐÚNG! Backend và Client giao tiếp qua WebSocket bằng session. Backend kiểm tra SimpUserRegistry để tìm session(s) của userId, rồi bắn message đến session(s) đó!** ✅

---

## 📚 Related Documentation

- **WEBSOCKET_CONFIG_EXPLAINED.md** - Configuration details
- **WEBSOCKET_SERVICE_EXPLAINED.md** - Service layer
- **WEBSOCKET_HOOK_EXPLAINED.md** - Frontend integration
- **TROUBLESHOOTING.md** - Common issues

---

**Last Updated**: 2025-11-16
