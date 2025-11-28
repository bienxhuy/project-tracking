# WebSocket Documentation Index

## 📚 Complete Documentation

Đây là complete documentation giải thích chi tiết implementation của WebSocket real-time notifications trong project.

---

## 🎯 Overview

### Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    Backend                               │
│                                                          │
│  WebSocketConfig.java                                   │
│    ↓ Configure WebSocket + STOMP                        │
│  WebSocketNotificationService.java                      │
│    ↓ Send messages to clients                           │
│  NotificationServiceImpl.java                           │
│    ↓ Create notification + trigger WebSocket            │
│                                                          │
└─────────────┬───────────────────────────────────────────┘
              │
              │ WebSocket Connection (STOMP + SockJS)
              │
┌─────────────┴───────────────────────────────────────────┐
│                   Frontend                               │
│                                                          │
│  websocket.service.ts                                   │
│    ↓ Manage WebSocket connection                        │
│  useWebSocketNotification.ts                            │
│    ↓ React hook for state management                    │
│  Components (NotificationList, NotificationBell)        │
│    ↓ UI rendering                                       │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

#### 5. **MESSAGE_BROKER_EXPLAINED.md**
**Location**: `/docs/websocket/MESSAGE_BROKER_EXPLAINED.md`

**Nội dung:**
- ✅ Message Broker là gì và vai trò
- ✅ Message routing mechanisms
- ✅ Subscription management
- ✅ Destination patterns (/topic, /queue, /user)
- ✅ SimpleBroker vs External Broker
- ✅ Message flow với broker
- ✅ Configuration examples
- ✅ Clustering và scalability
- ✅ Persistence và delivery guarantees
- ✅ Production best practices

**Key Topics:**
```
1. Message Broker Basics
   - What is a Message Broker
   - Role as middleware
   - Producer-Consumer pattern
2. Core Responsibilities
   - Message routing
   - Subscription registry
   - Destination matching
   - Message delivery
   - Session lifecycle
3. Destination Patterns
   - /topic/* (Broadcast)
   - /queue/* (Point-to-point)
   - /user/* (User-specific)
4. SimpleBroker (In-Memory)
   - Configuration
   - Pros and cons
   - Use cases
5. External Brokers
   - RabbitMQ configuration
   - Redis configuration
   - Clustering support
   - Persistence
6. Message Flow
   - Producer to Broker
   - Broker to Consumer
   - Complete routing process
7. Comparison
   - SimpleBroker vs External
   - When to use which
```

---

#### 6. **SESSION_ROUTING_EXPLAINED.md**
**Location**: `/docs/websocket/SESSION_ROUTING_EXPLAINED.md`

**Nội dung:**
- ✅ WebSocket session management chi tiết
- ✅ Principal (userId) extraction from JWT
- ✅ SimpUserRegistry internal workings
- ✅ Session lifecycle (connect, subscribe, disconnect)
- ✅ Client destination vs backend destination mapping
- ✅ Multi-tab/multi-device support
- ✅ User-specific routing process
- ✅ Broadcast vs point-to-point routing
- ✅ Session cleanup and timeout
- ✅ Debug endpoints for testing
- ✅ Real-world scenarios and examples

**Key Topics:**
```
1. Session Basics
   - What is a WebSocket session
   - Session ID vs Principal
   - Session storage in SimpUserRegistry
2. Connection Flow
   - Client connects with JWT
   - Principal extraction
   - Session creation
3. Subscription Mapping
   - Client: /user/queue/notifications
   - Backend: /user/123/queue/notifications
   - Spring automatic expansion
4. Message Routing
   - convertAndSendToUser() internals
   - Session lookup by principal
   - Multi-session delivery
5. Multi-Tab Support
   - One user, multiple sessions
   - Message delivery to all sessions
   - Session cleanup on tab close
6. Security
   - JWT validation
   - Principal setup
   - StompPrincipal implementation
7. Testing & Debugging
   - Debug endpoints
   - Session inspection
   - Test message sending
8. Common Scenarios
   - Multi-device login
   - Tab closure
   - Session timeout
```

---

#### 7. **TROUBLESHOOTING.md**
**Location**: `/docs/websocket/TROUBLESHOOTING.md`

**Nội dung:**
- ✅ Common backend issues (connection refused, CORS, authentication)
- ✅ Common frontend issues (SockJS fallback, auto-reconnect, memory leaks)
- ✅ Performance issues (high memory, slow processing)
- ✅ Testing issues (mocking, test failures)
- ✅ Debugging tools (DevTools, Actuator, custom endpoints)
- ✅ Best practices (Do's and Don'ts)
- ✅ Step-by-step solutions with code examples

**Key Topics:**
```
1. Backend Issues (Issue 1-4)
   - Connection refused
   - CORS errors
   - Messages not sent
   - Authentication failures
2. Frontend Issues (Issue 5-9)
   - SockJS fallback problems
   - Auto-reconnect not working
   - Memory leaks
   - UI not updating
   - Multiple connections
3. Performance Issues (Issue 10-11)
   - High memory usage
   - Slow message processing
   - Array size management
4. Testing Issues (Issue 12)
   - Mock setup
   - Test failures
5. Debugging Tools
   - Browser DevTools
   - Spring Actuator
   - Custom debug endpoints
6. Best Practices
   - Do's: cleanup, functional updates, error handling
   - Don'ts: stale state, multiple connections, blocking UI
```

---

## 📖 Documentation Files

### Backend Documentation

#### 1. **WEBSOCKET_CONFIG_EXPLAINED.md**
**Location**: `/backend/docs/websocket/WEBSOCKET_CONFIG_EXPLAINED.md`

**Nội dung:**
- ✅ WebSocketConfig.java chi tiết
- ✅ @EnableWebSocketMessageBroker annotation
- ✅ Message broker configuration
- ✅ STOMP endpoints registration
- ✅ `/topic` vs `/queue` destinations
- ✅ SockJS fallback mechanism
- ✅ CORS configuration
- ✅ Authentication setup
- ✅ Performance tuning
- ✅ Complete message flow diagrams

**Key Topics:**
```
1. Package và Imports
2. Class Declaration với Annotations
3. configureMessageBroker() method
   - enableSimpleBroker()
   - setApplicationDestinationPrefixes()
   - setUserDestinationPrefix()
4. registerStompEndpoints() method
   - addEndpoint()
   - setAllowedOriginPatterns()
   - withSockJS()
5. Message Flow Diagrams
6. Security Considerations
7. Performance Tuning
8. Testing Guide
```

---

#### 2. **WEBSOCKET_SERVICE_EXPLAINED.md**
**Location**: `/backend/docs/websocket/WEBSOCKET_SERVICE_EXPLAINED.md`

**Nội dung:**
- ✅ WebSocketNotificationService.java chi tiết
- ✅ SimpMessagingTemplate usage
- ✅ sendNotificationToUser() method
- ✅ broadcastNotification() method
- ✅ sendNotificationCount() method
- ✅ notifyNotificationRead() method
- ✅ convertToWebSocketMessage() method
- ✅ Error handling strategies
- ✅ Complete usage examples

**Key Topics:**
```
1. Imports và Annotations
   - @Slf4j, @Service, @RequiredArgsConstructor
2. Dependency Injection
   - SimpMessagingTemplate
3. sendNotificationToUser() 
   - convertAndSendToUser() deep dive
   - User-specific routing
   - Error handling
4. broadcastNotification()
   - Broadcast vs point-to-point
5. sendNotificationCount()
   - Lightweight updates
6. notifyNotificationRead()
   - Multi-tab synchronization
7. convertToWebSocketMessage()
   - Entity → DTO conversion
   - Null-safe field extraction
8. Complete Usage Flow
9. Testing Guide
10. Performance Considerations
11. Security Best Practices
```

---

### Frontend Documentation

#### 3. **WEBSOCKET_SERVICE_EXPLAINED.md**
**Location**: `/frontend/docs/websocket/WEBSOCKET_SERVICE_EXPLAINED.md`

**Nội dung:**
- ✅ websocket.service.ts chi tiết
- ✅ STOMP Client configuration
- ✅ SockJS fallback
- ✅ Connection management
- ✅ Subscription handling
- ✅ Callback system (Observer pattern)
- ✅ Auto-reconnect logic
- ✅ Singleton pattern
- ✅ TypeScript type safety

**Key Topics:**
```
1. Imports
   - @stomp/stompjs
   - sockjs-client
2. Type Definitions
   - WebSocketNotification interface
   - Callback types
3. Class Declaration
   - Private fields
   - Callback arrays
4. connect() method
   - Environment variables
   - Client configuration
   - webSocketFactory
   - connectHeaders (JWT)
   - Heartbeats
5. Connection Callbacks
   - onConnect
   - onStompError
   - onWebSocketError
   - onDisconnect
6. subscribeToTopics()
   - Multiple subscriptions
   - Message parsing
   - Callback notification
7. handleReconnect()
   - Exponential backoff
   - Max attempts
8. Callback Registration
   - Observer pattern
   - Unsubscribe functions
9. Singleton Pattern
10. Complete Message Flow
11. Testing Guide
12. Performance Considerations
```

---

---

#### 4. **WEBSOCKET_HOOK_EXPLAINED.md**
**Location**: `/frontend/docs/websocket/WEBSOCKET_HOOK_EXPLAINED.md`

**Nội dung:**
- ✅ useWebSocketNotification.ts chi tiết
- ✅ React hooks lifecycle (useState, useEffect)
- ✅ State management với TypeScript generics
- ✅ useEffect dependencies và cleanup
- ✅ Functional state updates
- ✅ Custom hook pattern
- ✅ Observer pattern callbacks
- ✅ Memory leak prevention
- ✅ Multiple component usage
- ✅ Testing strategies
- ✅ Performance considerations

**Key Topics:**
```
1. Imports và Dependencies
   - React hooks (useState, useEffect)
   - WebSocket service integration
2. Hook Declaration
   - Custom hook naming convention
   - Arrow function syntax
3. State Management
   - notifications state (array)
   - unreadCount state (number)
   - isConnected state (boolean)
   - TypeScript generics
4. useEffect Side Effects
   - WebSocket connection
   - Callback registration
   - Cleanup functions
   - Empty dependency array
5. Callback Implementations
   - onNotification (functional updates)
   - onNotificationCount
   - onConnectionChange
6. Return Object
   - Named exports
   - Component destructuring
7. Design Patterns
   - Custom Hook Pattern
   - Observer Pattern
   - Singleton Pattern (service)
8. Usage Examples
   - NotificationList component
   - NotificationBell component
   - Multiple components
9. Performance
   - Functional updates vs direct
   - Array operations
   - Multiple hook instances
10. Testing
    - Unit tests
    - Integration tests
11. Common Issues
    - Memory leaks
    - Stale state
    - Multiple connections
```

---

## 🔍 Quick Reference

### Backend Classes

| Class | Purpose | Key Methods |
|-------|---------|-------------|
| **WebSocketConfig** | Configure WebSocket | `configureMessageBroker()`, `registerStompEndpoints()` |
| **WebSocketNotificationService** | Send messages | `sendNotificationToUser()`, `broadcastNotification()` |
| **WebSocketNotificationMessage** | DTO | Builder pattern, fields |

### Frontend Services

| File | Purpose | Key Methods |
|------|---------|-------------|
| **websocket.service.ts** | Connection manager | `connect()`, `subscribe()`, `onNotification()` |
| **useWebSocketNotification.ts** | React hook | State management, lifecycle |

---

## 📊 Message Destinations

### User-Specific (Private)

| Destination | Purpose | Payload Type |
|-------------|---------|--------------|
| `/user/queue/notifications` | New notifications | `WebSocketNotificationMessage` |
| `/user/queue/notification-count` | Unread count updates | `number` |
| `/user/queue/notification-updates` | Status updates (read/delete) | `WebSocketNotificationMessage` (minimal) |

### Broadcast (Public)

| Destination | Purpose | Payload Type |
|-------------|---------|--------------|
| `/topic/notifications` | System-wide announcements | `WebSocketNotificationMessage` |

---

## 🔄 Complete Flow Example

### New Notification Flow

```
1. Backend: Task assigned to user
   ↓
2. NotificationServiceImpl.createNotification()
   - Save to database
   ↓
3. WebSocketNotificationService.convertToWebSocketMessage()
   - Convert Entity → DTO
   ↓
4. WebSocketNotificationService.sendNotificationToUser()
   - SimpMessagingTemplate.convertAndSendToUser()
   ↓
5. Message Broker routes to user sessions
   - /user/123/queue/notifications
   ↓
6. WebSocket transmits to client(s)
   - All open tabs receive
   ↓
7. websocket.service.ts receives
   - STOMP subscription callback
   ↓
8. Parse JSON + notify callbacks
   - this.notificationCallbacks.forEach()
   ↓
9. useWebSocketNotification hook
   - Update React state
   ↓
10. Component re-renders
    - New notification appears in UI ✅
```

---

## 🧪 Testing Checklist

### Backend
- [ ] WebSocket endpoint accessible (`ws://localhost:9090/ws`)
- [ ] STOMP handshake successful
- [ ] Can subscribe to `/user/queue/notifications`
- [ ] Message sending works
- [ ] Multiple clients receive messages
- [ ] Error handling works

### Frontend
- [ ] Connection establishes
- [ ] Subscriptions created
- [ ] Messages received and parsed
- [ ] State updates trigger re-renders
- [ ] Auto-reconnect works
- [ ] Cleanup prevents memory leaks

---

## 🐛 Common Issues

### Backend

**Issue**: WebSocket not connecting
```
Solution: Check CORS configuration in WebSocketConfig
```

**Issue**: Messages not received
```
Solution: Verify destination paths match
```

**Issue**: Multiple instances cause issues
```
Solution: Use external message broker (Redis/RabbitMQ)
```

### Frontend

**Issue**: SockJS fallback not working
```
Solution: Check SockJS endpoints are accessible
```

**Issue**: Memory leaks
```
Solution: Ensure unsubscribe functions are called
```

**Issue**: State not updating
```
Solution: Check useEffect dependencies
```

---

## 📚 External Resources

### Official Documentation
- [Spring WebSocket](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [STOMP Protocol](https://stomp.github.io/)
- [SockJS Protocol](https://github.com/sockjs/sockjs-protocol)
- [@stomp/stompjs](https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html)

### Tutorials
- [Spring Boot WebSocket Tutorial](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [React WebSocket Integration](https://react.dev/learn)

---

## 🎯 Next Steps

1. **Read in order:**
   - Start with `WEBSOCKET_CONFIG_EXPLAINED.md`
   - Then `WEBSOCKET_SERVICE_EXPLAINED.md`
   - Finally frontend documentation

2. **Hands-on practice:**
   - Follow testing guides
   - Try examples
   - Build custom features

3. **Advanced topics:**
   - External message broker
   - Load balancing
   - Monitoring and metrics

---

## 📝 Document Updates

| Date | File | Changes |
|------|------|---------|
| 2025-11-16 | WEBSOCKET_CONFIG_EXPLAINED.md | Initial creation |
| 2025-11-16 | WEBSOCKET_SERVICE_EXPLAINED.md | Initial creation |
| 2025-11-16 | Frontend WEBSOCKET_SERVICE_EXPLAINED.md | Initial creation |
| 2025-11-16 | INDEX.md | Initial creation |

---

**Happy Learning! 🚀**

Nếu có thắc mắc về bất kỳ phần nào, hãy refer đến file documentation tương ứng để xem giải thích chi tiết.
