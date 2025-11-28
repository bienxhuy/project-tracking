# WebSocket Troubleshooting Guide

## 🔍 Common Issues và Solutions

---

## Backend Issues

### ❌ Issue 1: WebSocket Connection Refused

**Error Message:**
```
Failed to connect to ws://localhost:9090/ws
WebSocket connection failed: Error in connection establishment: net::ERR_CONNECTION_REFUSED
```

**Possible Causes:**
1. Backend server không running
2. Port 9090 đã được sử dụng bởi app khác
3. Firewall blocking connection

**Solutions:**

**Solution 1: Check Backend Running**
```bash
# Check if Spring Boot running
ps aux | grep java

# Check port 9090
lsof -i :9090

# Or netstat
netstat -an | grep 9090
```

**Solution 2: Start Backend**
```bash
cd backend
./mvnw spring-boot:run
```

**Solution 3: Change Port**
```properties
# application.properties
server.port=8080
```

```env
# frontend/.env
VITE_WS_URL=http://localhost:8080/ws
```

---

### ❌ Issue 2: CORS Error

**Error Message:**
```
Access to XMLHttpRequest at 'http://localhost:9090/ws' from origin 'http://localhost:5173' 
has been blocked by CORS policy
```

**Cause:**
- Frontend origin không được allow trong WebSocket config

**Solution:**

```java
// WebSocketConfig.java
@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")  // Allow all origins (dev only!)
            .withSockJS();
}
```

**Production Solution:**
```java
// Specific origins only
.setAllowedOriginPatterns(
    "https://yourdomain.com",
    "https://www.yourdomain.com"
)
```

---

### ❌ Issue 3: Messages Not Sent

**Symptoms:**
- WebSocket connected
- No messages received

**Debug Steps:**

**Step 1: Check Backend Logs**
```bash
# Look for errors
tail -f logs/spring.log
```

**Step 2: Verify Service Called**
```java
@Slf4j
@Service
public class WebSocketNotificationService {
    public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
        log.info("Sending notification to user {}: {}", userId, message); // Add this
        simpMessagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            message
        );
    }
}
```

**Step 3: Check User Sessions**
```java
// Add debug endpoint
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    
    @GetMapping("/websocket-users")
    public Set<String> getConnectedUsers() {
        return simpUserRegistry.getUsers().stream()
            .map(SimpUser::getName)
            .collect(Collectors.toSet());
    }
}
```

**Step 4: Test with Browser Console**
```javascript
// In frontend console
webSocketService.connect();

// Should see connection logs
```

---

### ❌ Issue 4: Authentication Failed

**Error Message:**
```
STOMP ERROR: Authentication failed
```

**Cause:**
- JWT token invalid or expired
- Token not sent in headers

**Solution:**

**Check Token Sent:**
```typescript
// websocket.service.ts
this.client.connectHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}` // Ensure token exists
};

console.log('Token:', localStorage.getItem('token')); // Debug
```

**Backend: Verify Interceptor**
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
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                    log.info("WebSocket auth token: {}", authToken); // Debug
                    
                    // Validate token...
                }
                
                return message;
            }
        });
    }
}
```

---

## Frontend Issues

### ❌ Issue 5: SockJS Fallback Not Working

**Error:**
```
WebSocket connection failed, trying SockJS...
SockJS connection failed: 404 Not Found
```

**Cause:**
- SockJS endpoints not properly configured

**Solution:**

**Backend:**
```java
@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();  // ✅ Enable SockJS
}
```

**Frontend:**
```typescript
// Ensure SockJS URL correct
const socket = new SockJS(`${import.meta.env.VITE_WS_URL}`);
// Should be: http://localhost:9090/ws (not ws://...)
```

---

### ❌ Issue 6: Auto-Reconnect Not Working

**Symptoms:**
- Disconnect happens
- No reconnection attempt

**Debug:**

```typescript
// websocket.service.ts - Add logs
private handleReconnect(): void {
  if (this.reconnectAttempts < this.maxReconnectAttempts) {
    console.log(`Reconnect attempt ${this.reconnectAttempts + 1}`); // Add this
    
    const delay = Math.min(
      this.baseReconnectDelay * this.reconnectAttempts,
      this.maxReconnectDelay
    );
    
    console.log(`Waiting ${delay}ms before reconnect`); // Add this
    
    setTimeout(() => {
      this.reconnectAttempts++;
      this.connect();
    }, delay);
  } else {
    console.error('Max reconnect attempts reached'); // Add this
  }
}
```

**Check Network:**
```bash
# Ping backend
curl http://localhost:9090/actuator/health
```

---

### ❌ Issue 7: Memory Leak in React

**Warning:**
```
Warning: Can't perform a React state update on an unmounted component.
This is a no-op, but it indicates a memory leak in your application.
```

**Cause:**
- WebSocket callback called after component unmounts
- Cleanup function not executed

**Solution:**

**Verify Cleanup:**
```typescript
// useWebSocketNotification.ts
useEffect(() => {
  webSocketService.connect();

  const unsub1 = webSocketService.onNotification((notification) => {
    setNotifications((prev) => [notification, ...prev]);
  });

  const unsub2 = webSocketService.onNotificationCount((count) => {
    setUnreadCount(count);
  });

  const unsub3 = webSocketService.onConnectionChange((connected) => {
    setIsConnected(connected);
  });

  // ✅ MUST return cleanup
  return () => {
    unsub1();
    unsub2();
    unsub3();
    webSocketService.disconnect();
  };
}, []); // ✅ Empty array
```

**Test Cleanup:**
```typescript
// Add console.log in cleanup
return () => {
  console.log('Cleaning up WebSocket...'); // Should see on unmount
  unsub1();
  unsub2();
  unsub3();
  webSocketService.disconnect();
};
```

---

### ❌ Issue 8: Notifications Not Updating UI

**Symptoms:**
- WebSocket messages received (in console)
- UI doesn't update

**Debug:**

**Check State Updates:**
```typescript
const unsub1 = webSocketService.onNotification((notification) => {
  console.log('Notification received:', notification); // Add this
  setNotifications((prev) => {
    const updated = [notification, ...prev];
    console.log('Updated notifications:', updated); // Add this
    return updated;
  });
});
```

**Check Component Rendering:**
```typescript
const NotificationList: React.FC = () => {
  const { notifications } = useWebSocketNotification();
  
  console.log('Rendering with notifications:', notifications); // Add this
  
  return (
    <div>
      {notifications.map(n => (
        <div key={n.id}>{n.title}</div>
      ))}
    </div>
  );
};
```

**Check Key Props:**
```typescript
// ❌ BAD (no key)
{notifications.map(n => <div>{n.title}</div>)}

// ✅ GOOD (unique key)
{notifications.map(n => <div key={n.id}>{n.title}</div>)}
```

---

### ❌ Issue 9: Multiple WebSocket Connections

**Symptoms:**
- Network tab shows multiple WS connections
- Duplicate messages received

**Cause:**
- Multiple components calling `webSocketService.connect()`
- No proper disconnect

**Solution:**

**Use Singleton Pattern:**
```typescript
// websocket.service.ts
class WebSocketService {
  private client: Client | null = null;
  private isConnecting: boolean = false;
  
  connect(): void {
    // ✅ Prevent multiple connections
    if (this.client?.active || this.isConnecting) {
      console.log('Already connected or connecting');
      return;
    }
    
    this.isConnecting = true;
    this.client = new Client({
      // config...
    });
    
    this.client.activate();
  }
  
  disconnect(): void {
    if (this.client?.active) {
      this.client.deactivate();
      this.client = null;
      this.isConnecting = false;
    }
  }
}

// ✅ Export singleton
export const webSocketService = new WebSocketService();
```

**Use Effect Properly:**
```typescript
// ❌ BAD (reconnects on every render)
useEffect(() => {
  webSocketService.connect();
}); // No dependency array!

// ✅ GOOD (connects once)
useEffect(() => {
  webSocketService.connect();
  return () => webSocketService.disconnect();
}, []); // Empty array
```

---

## Performance Issues

### ⚠️ Issue 10: High Memory Usage

**Symptoms:**
- Browser memory increases over time
- Page becomes slow

**Causes:**
1. Unbounded notifications array
2. Callbacks not unregistered
3. Large message payloads

**Solutions:**

**Limit Array Size:**
```typescript
const unsub1 = webSocketService.onNotification((notification) => {
  setNotifications((prev) => 
    [notification, ...prev].slice(0, 50) // Keep only 50
  );
});
```

**Paginate Notifications:**
```typescript
const [notifications, setNotifications] = useState<WebSocketNotification[]>([]);
const [displayCount, setDisplayCount] = useState(20);

const displayedNotifications = notifications.slice(0, displayCount);

const loadMore = () => setDisplayCount(prev => prev + 20);
```

**Clean Old Notifications:**
```typescript
useEffect(() => {
  const cleanup = setInterval(() => {
    setNotifications(prev => 
      prev.filter(n => 
        Date.now() - new Date(n.timestamp).getTime() < 24 * 60 * 60 * 1000
      )
    );
  }, 60 * 60 * 1000); // Every hour
  
  return () => clearInterval(cleanup);
}, []);
```

---

### ⚠️ Issue 11: Slow Message Processing

**Symptoms:**
- Delay between message received and UI update
- UI freezes when many messages arrive

**Solutions:**

**Batch Updates:**
```typescript
// Instead of updating on every message
const [pendingNotifications, setPendingNotifications] = useState<WebSocketNotification[]>([]);

useEffect(() => {
  const flushInterval = setInterval(() => {
    if (pendingNotifications.length > 0) {
      setNotifications(prev => [...pendingNotifications, ...prev]);
      setPendingNotifications([]);
    }
  }, 1000); // Flush every second
  
  return () => clearInterval(flushInterval);
}, [pendingNotifications]);
```

**Virtual Scrolling:**
```typescript
import { FixedSizeList } from 'react-window';

const NotificationList: React.FC = () => {
  const { notifications } = useWebSocketNotification();
  
  return (
    <FixedSizeList
      height={600}
      itemCount={notifications.length}
      itemSize={80}
      width="100%"
    >
      {({ index, style }) => (
        <div style={style}>
          {notifications[index].title}
        </div>
      )}
    </FixedSizeList>
  );
};
```

---

## Testing Issues

### 🧪 Issue 12: Tests Failing

**Error:**
```
TypeError: Cannot read property 'connect' of undefined
```

**Cause:**
- WebSocket service not mocked in tests

**Solution:**

```typescript
// __mocks__/websocket.service.ts
export const webSocketService = {
  connect: jest.fn(),
  disconnect: jest.fn(),
  onNotification: jest.fn(() => jest.fn()),
  onNotificationCount: jest.fn(() => jest.fn()),
  onConnectionChange: jest.fn(() => jest.fn()),
};
```

```typescript
// test file
jest.mock('../services/websocket.service');

import { webSocketService } from '../services/websocket.service';

describe('useWebSocketNotification', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should connect on mount', () => {
    renderHook(() => useWebSocketNotification());
    expect(webSocketService.connect).toHaveBeenCalled();
  });
});
```

---

## Debugging Tools

### Tool 1: Browser DevTools

**Network Tab:**
```
1. Open DevTools (F12)
2. Go to Network tab
3. Filter: WS (WebSocket)
4. Click connection to see:
   - Handshake headers
   - Messages sent/received
   - Close codes
```

**Console Logs:**
```typescript
// Enable debug in service
this.client = new Client({
  debug: (str) => {
    console.log('STOMP Debug:', str); // See all STOMP frames
  },
  // ...
});
```

---

### Tool 2: Spring Boot Actuator

**Enable WebSocket Metrics:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,metrics,websocket
```

**Check Metrics:**
```bash
# Active sessions
curl http://localhost:9090/actuator/metrics/spring.websocket.sessions

# Message counts
curl http://localhost:9090/actuator/metrics/spring.websocket.messages.sent
```

---

### Tool 3: Custom Debug Endpoint

```java
@RestController
@RequestMapping("/api/debug")
public class WebSocketDebugController {
    
    @Autowired
    private SimpUserRegistry userRegistry;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/connected-users")
    public Map<String, Object> getConnectedUsers() {
        Set<SimpUser> users = userRegistry.getUsers();
        
        return Map.of(
            "totalUsers", users.size(),
            "users", users.stream()
                .map(user -> Map.of(
                    "name", user.getName(),
                    "sessionCount", user.getSessions().size()
                ))
                .collect(Collectors.toList())
        );
    }
    
    @PostMapping("/test-notification/{userId}")
    public void sendTestNotification(@PathVariable Long userId) {
        WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
            .id(999L)
            .title("Test Notification")
            .message("This is a test")
            .type("INFO")
            .timestamp(LocalDateTime.now().toString())
            .build();
            
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            message
        );
    }
}
```

**Usage:**
```bash
# Check connected users
curl http://localhost:9090/api/debug/connected-users

# Send test notification
curl -X POST http://localhost:9090/api/debug/test-notification/123
```

---

## Best Practices

### ✅ Do's

1. **Always cleanup subscriptions**
   ```typescript
   return () => {
     unsubscribe();
     disconnect();
   };
   ```

2. **Use functional state updates**
   ```typescript
   setNotifications(prev => [notification, ...prev]);
   ```

3. **Limit array sizes**
   ```typescript
   [notification, ...prev].slice(0, 50)
   ```

4. **Handle connection errors gracefully**
   ```typescript
   onStompError: (frame) => {
     console.error('STOMP error:', frame);
     this.handleReconnect();
   }
   ```

5. **Use TypeScript for type safety**
   ```typescript
   useState<WebSocketNotification[]>([])
   ```

### ❌ Don'ts

1. **Don't ignore cleanup**
   ```typescript
   // ❌ Missing cleanup
   useEffect(() => {
     connect();
     // Missing return cleanup!
   }, []);
   ```

2. **Don't use stale state**
   ```typescript
   // ❌ Uses stale state
   setNotifications([notification, ...notifications]);
   ```

3. **Don't create multiple connections**
   ```typescript
   // ❌ Connects on every render
   useEffect(() => {
     connect();
   }); // Missing dependency array!
   ```

4. **Don't block UI thread**
   ```typescript
   // ❌ Synchronous heavy processing
   onNotification((n) => {
     for (let i = 0; i < 1000000; i++) { /* heavy work */ }
   });
   ```

5. **Don't hardcode URLs**
   ```typescript
   // ❌ Hardcoded
   const url = 'ws://localhost:9090/ws';
   
   // ✅ Environment variable
   const url = import.meta.env.VITE_WS_URL;
   ```

---

## Getting Help

### Resources

1. **Documentation**
   - `/docs/websocket/INDEX.md`
   - `/backend/docs/websocket/`
   - `/frontend/docs/websocket/`

2. **Official Docs**
   - [Spring WebSocket](https://docs.spring.io/spring-framework/reference/web/websocket.html)
   - [STOMP.js](https://stomp-js.github.io/)
   - [React Hooks](https://react.dev/reference/react)

3. **Logs**
   - Backend: Check Spring Boot console
   - Frontend: Check browser console
   - Network: Check DevTools Network tab

---

**Happy Debugging! 🐛🔧**

Most issues can be resolved by checking logs and verifying configurations. Don't hesitate to add debug logs to understand the flow!
