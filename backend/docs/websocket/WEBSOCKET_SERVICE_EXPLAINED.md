# WebSocketNotificationService.java - Chi tiết giải thích

## 📁 File Location
```
backend/src/main/java/POSE_Project_Tracking/Blog/service/WebSocketNotificationService.java
```

## 🎯 Mục đích
Service layer để gửi WebSocket notifications đến connected clients. Đây là layer trung gian giữa business logic và WebSocket infrastructure.

---

## 📝 Code với Giải thích chi tiết

### 1. Imports và Annotations

```java
package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.WebSocketNotificationMessage;
import POSE_Project_Tracking.Blog.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
```

**Giải thích imports quan trọng:**

#### `SimpMessagingTemplate`
- **Package**: `org.springframework.messaging.simp`
- **Mục đích**: Core class để gửi messages qua WebSocket
- **SIMP**: Simple Messaging Protocol (wrapper around STOMP)
- **Tương tự**: JdbcTemplate, RestTemplate trong Spring

**SimpMessagingTemplate methods:**
```java
// Send to a destination
void convertAndSend(String destination, Object payload)

// Send to a specific user
void convertAndSendToUser(String user, String destination, Object payload)

// Send with headers
void convertAndSendToUser(String user, String destination, Object payload, Map<String, Object> headers)
```

#### Annotations

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {
```

**`@Slf4j` (Lombok)**
- **Mục đích**: Tự động tạo logger instance
- **Equivalent code**:
```java
private static final org.slf4j.Logger log = 
    org.slf4j.LoggerFactory.getLogger(WebSocketNotificationService.class);
```
- **Usage**: `log.info()`, `log.error()`, `log.debug()`

**`@Service`**
- **Mục đích**: Đánh dấu class là Spring service bean
- **Hoạt động**: 
  - Spring tự động scan và register bean
  - Có thể inject vào other components
  - Support transaction management

**`@RequiredArgsConstructor` (Lombok)**
- **Mục đích**: Tự động generate constructor cho final fields
- **Equivalent code**:
```java
public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
}
```

---

### 2. Dependency Injection

```java
private final SimpMessagingTemplate messagingTemplate;
```

**Giải thích:**

#### **`final` keyword**
- **Bắt buộc**: Field không thể reassign sau constructor
- **Best practice**: Immutability, thread-safety
- **Constructor injection**: Lombok tự generate

#### **`SimpMessagingTemplate` là gì?**

**Khởi tạo bởi Spring:**
```java
// Spring tự động config (trong WebSocketConfig)
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // Spring automatically creates SimpMessagingTemplate bean
    // và inject vào services cần thiết
}
```

**Internal structure:**
```java
public class SimpMessagingTemplate {
    private MessageChannel messageChannel;
    private MessageConverter messageConverter;
    private String destinationPrefix = "/user";
    
    // Methods to send messages
    public void convertAndSend(String destination, Object payload) {
        Message<?> message = doConvert(payload, headers);
        messageChannel.send(message);
    }
}
```

**Thread-safety:**
- ✅ SimpMessagingTemplate is thread-safe
- ✅ Can be used concurrently by multiple threads
- ✅ Internally uses thread-safe MessageChannel

---

### 3. Method: `sendNotificationToUser()`

```java
public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
    try {
        String destination = "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            destination, 
            message
        );
        log.info("Sent WebSocket notification to user {}: {}", userId, message.getTitle());
    } catch (Exception e) {
        log.error("Error sending WebSocket notification to user {}: {}", userId, e.getMessage());
    }
}
```

#### **Line-by-line Breakdown:**

#### **Line 1: Method signature**
```java
public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message)
```
- **Return**: `void` - fire and forget
- **Parameters**:
  - `userId`: User ID to send notification to
  - `message`: Notification payload (DTO)

#### **Line 2: Try-catch block**
```java
try {
    // Send logic
} catch (Exception e) {
    log.error(...);
}
```
**Tại sao cần try-catch?**
- ✅ WebSocket connection có thể disconnect
- ✅ Message serialization có thể fail
- ✅ Không muốn crash entire application nếu notification fails
- ✅ Graceful degradation

#### **Line 3: Destination**
```java
String destination = "/queue/notifications";
```

**Phân tích destination:**
```
/queue/notifications
  │      │
  │      └─> Specific queue name
  └─────────> Queue prefix (từ WebSocketConfig)
```

**Full destination path:**
```
Client subscribes:  /user/queue/notifications
Spring converts:    /user/{userId}/queue/notifications
Actual path:        /user/123/queue/notifications
```

**Matching logic:**
```
Server sends to user 123:
  convertAndSendToUser("123", "/queue/notifications", msg)
  
Spring routing:
  1. Prefix với "/user"
  2. Thêm userId
  3. Final: /user/123/queue/notifications
  
Client subscribed to:
  /user/queue/notifications
  
Spring matching:
  User session has userId = 123
  → Match! → Deliver message
```

#### **Line 4-7: Send message**
```java
messagingTemplate.convertAndSendToUser(
    userId.toString(),    // User identifier
    destination,          // Queue destination
    message              // Payload to send
);
```

**Deep dive vào `convertAndSendToUser()`:**

**Method signature:**
```java
void convertAndSendToUser(
    String user,           // User identifier (string)
    String destination,    // Destination path
    Object payload        // Any object to send
)
```

**Internal processing:**

**Step 1: Convert payload to Message**
```java
// Spring uses MessageConverter (default: Jackson JSON)
Message<?> message = messageConverter.toMessage(payload, headers);

// Example:
WebSocketNotificationMessage msg = new WebSocketNotificationMessage(...);
// Converts to:
{
  "id": 123,
  "title": "New Task",
  "message": "You have been assigned...",
  "type": "TASK_ASSIGNMENT",
  "action": "NEW_NOTIFICATION"
}
```

**Step 2: Build full destination**
```java
String fullDestination = "/user/" + user + destination;
// Result: /user/123/queue/notifications
```

**Step 3: Find user sessions**
```java
// Spring maintains map of userId → sessionIds
Map<String, Set<String>> userSessionsMap;

Set<String> sessions = userSessionsMap.get("123");
// Example: ["session-abc", "session-xyz"]  (2 tabs open)
```

**Step 4: Send to all sessions**
```java
for (String sessionId : sessions) {
    WebSocketSession session = getSession(sessionId);
    if (session.isOpen()) {
        session.sendMessage(new TextMessage(jsonMessage));
    }
}
```

**Complete flow diagram:**
```
sendNotificationToUser(123, message)
  │
  ├─> userId.toString() → "123"
  │
  ├─> MessageConverter.toMessage()
  │     └─> Jackson JSON serialization
  │           └─> {"id": 123, "title": "..."}
  │
  ├─> Build destination: /user/123/queue/notifications
  │
  ├─> Find sessions for user 123
  │     └─> [session-abc, session-xyz]
  │
  └─> Send to each session
        ├─> session-abc.send(message) ✅
        └─> session-xyz.send(message) ✅
```

#### **Line 8: Logging**
```java
log.info("Sent WebSocket notification to user {}: {}", userId, message.getTitle());
```

**SLF4J Placeholder syntax:**
```java
log.info("Template with {} and {}", value1, value2);
// Runtime: "Template with value1 and value2"
```

**Performance benefit:**
```java
// BAD (always create string even if logging disabled)
log.info("Message: " + expensive.toString());

// GOOD (only evaluate if logging enabled)
log.info("Message: {}", expensive);
```

#### **Line 9-11: Error handling**
```java
} catch (Exception e) {
    log.error("Error sending WebSocket notification to user {}: {}", 
        userId, e.getMessage());
}
```

**Possible exceptions:**

1. **MessageConversionException**
   ```java
   // When JSON serialization fails
   throw new MessageConversionException("Cannot convert message");
   ```

2. **IllegalStateException**
   ```java
   // When WebSocket session closed
   throw new IllegalStateException("Session already closed");
   ```

3. **NullPointerException**
   ```java
   // When message or fields are null
   if (message == null) throw new NullPointerException();
   ```

**Error handling strategy:**
```java
try {
    messagingTemplate.convertAndSendToUser(...);
} catch (Exception e) {
    // Log error but don't throw
    // Application continues working
    // User may get notification via FCM fallback
    log.error("WebSocket failed, FCM will handle", e);
}
```

---

### 4. Method: `broadcastNotification()`

```java
public void broadcastNotification(WebSocketNotificationMessage message) {
    try {
        messagingTemplate.convertAndSend("/topic/notifications", message);
        log.info("Broadcasted WebSocket notification: {}", message.getTitle());
    } catch (Exception e) {
        log.error("Error broadcasting WebSocket notification: {}", e.getMessage());
    }
}
```

#### **Broadcast vs User-specific:**

| Aspect | `convertAndSend()` | `convertAndSendToUser()` |
|--------|-------------------|-------------------------|
| Destination | `/topic/notifications` | `/user/{id}/queue/notifications` |
| Recipients | ALL subscribers | Specific user |
| Use case | System announcements | Personal notifications |
| Privacy | Public | Private |

**Example usage:**

**Broadcast (System-wide):**
```java
// Maintenance notification
WebSocketNotificationMessage msg = WebSocketNotificationMessage.builder()
    .title("System Maintenance")
    .message("Server will restart in 10 minutes")
    .type(ENotificationType.SYSTEM)
    .action("NEW_NOTIFICATION")
    .build();

broadcastNotification(msg);
// → All connected users receive this
```

**User-specific:**
```java
// Task assignment
WebSocketNotificationMessage msg = WebSocketNotificationMessage.builder()
    .title("New Task Assigned")
    .message("You have been assigned to task #123")
    .type(ENotificationType.TASK_ASSIGNMENT)
    .action("NEW_NOTIFICATION")
    .build();

sendNotificationToUser(userId, msg);
// → Only user with userId receives this
```

**Broadcast flow:**
```
broadcastNotification(message)
  │
  ├─> convertAndSend("/topic/notifications", message)
  │
  ├─> Message Broker receives
  │
  ├─> Find ALL subscribers to /topic/notifications
  │     ├─> User 1 (session-a)
  │     ├─> User 2 (session-b)
  │     ├─> User 3 (session-c, session-d)  # 2 tabs
  │     └─> ...
  │
  └─> Send to ALL sessions
        ├─> session-a.send(message) ✅
        ├─> session-b.send(message) ✅
        ├─> session-c.send(message) ✅
        └─> session-d.send(message) ✅
```

**Client subscription:**
```javascript
// Subscribe to broadcasts
stompClient.subscribe("/topic/notifications", (message) => {
    console.log("Broadcast:", JSON.parse(message.body));
});
```

---

### 5. Method: `sendNotificationCount()`

```java
public void sendNotificationCount(Long userId, Long unreadCount) {
    try {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notification-count",
            unreadCount
        );
        log.debug("Sent notification count to user {}: {}", userId, unreadCount);
    } catch (Exception e) {
        log.error("Error sending notification count to user {}: {}", userId, e.getMessage());
    }
}
```

#### **Key differences:**

**Payload type:**
```java
// Previous methods: Complex DTO
WebSocketNotificationMessage message = ...

// This method: Simple number
Long unreadCount = 5L;
```

**JSON serialization:**
```json
// WebSocketNotificationMessage
{
  "id": 123,
  "title": "...",
  "message": "...",
  "type": "..."
}

// Long (primitive)
5
```

**Different destination:**
```java
"/queue/notifications"       // For full notification objects
"/queue/notification-count"  // For count updates only
```

**Purpose:**
- Update badge count in real-time
- Separate channel for performance
- Lightweight updates

**Client usage:**
```javascript
// Subscribe to count updates
stompClient.subscribe("/user/queue/notification-count", (message) => {
    const count = JSON.parse(message.body);  // Just a number: 5
    updateBadge(count);
});
```

**When to use:**
```java
// Scenario 1: New notification
createNotification(userId, ...);
sendNotificationToUser(userId, fullMessage);     // Send full notification
sendNotificationCount(userId, newCount);         // Update count

// Scenario 2: Mark as read
markNotificationAsRead(notificationId);
sendNotificationCount(userId, decrementedCount); // Just update count
```

**Log level: `log.debug()` vs `log.info()`**

```java
log.info(...)   // Important events, always logged
log.debug(...)  // Detailed info, only in debug mode
```

**Configuration (application.properties):**
```properties
# Production: INFO level
logging.level.POSE_Project_Tracking.Blog.service=INFO

# Development: DEBUG level
logging.level.POSE_Project_Tracking.Blog.service=DEBUG
```

---

### 6. Method: `notifyNotificationRead()`

```java
public void notifyNotificationRead(Long userId, Long notificationId) {
    try {
        WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
            .id(notificationId)
            .action("NOTIFICATION_READ")
            .build();
        
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notification-updates",
            message
        );
        log.debug("Notified user {} that notification {} was read", userId, notificationId);
    } catch (Exception e) {
        log.error("Error notifying read status to user {}: {}", userId, e.getMessage());
    }
}
```

#### **Builder pattern analysis:**

```java
WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
    .id(notificationId)
    .action("NOTIFICATION_READ")
    .build();
```

**Equivalent without builder:**
```java
WebSocketNotificationMessage message = new WebSocketNotificationMessage();
message.setId(notificationId);
message.setAction("NOTIFICATION_READ");
// All other fields are null
```

**Builder advantage:**
- ✅ Immutable object construction
- ✅ Clear intent
- ✅ Null safety
- ✅ Flexible (optional fields)

**Minimal message:**
```json
{
  "id": 123,
  "action": "NOTIFICATION_READ",
  "title": null,
  "message": null,
  "type": null
  // ... other fields null
}
```

**Why minimal?**
- Client only needs to know:
  1. Which notification (id)
  2. What happened (action)
- Reduces network payload
- Client already has full notification data

**Multi-tab synchronization:**

**Scenario:**
```
User has 3 tabs open:
  Tab 1 (active)
  Tab 2 (background)
  Tab 3 (background)

Tab 1: User clicks notification → mark as read
  ↓
Backend: markAsRead(notificationId)
  ↓
Backend: notifyNotificationRead(userId, notificationId)
  ↓
WebSocket broadcast to ALL user sessions:
  ├─> Tab 1: Update UI (already read)
  ├─> Tab 2: Update UI (mark as read) ✅
  └─> Tab 3: Update UI (mark as read) ✅

Result: All tabs synchronized!
```

**Client handling:**
```javascript
stompClient.subscribe("/user/queue/notification-updates", (message) => {
    const update = JSON.parse(message.body);
    
    if (update.action === "NOTIFICATION_READ") {
        // Find notification in local state
        const notification = notifications.find(n => n.id === update.id);
        if (notification) {
            notification.isRead = true;  // Update UI
        }
    }
});
```

**Different destination again:**
```
/queue/notifications        → New notifications
/queue/notification-count   → Count updates
/queue/notification-updates → Status updates (read/delete)
```

**Why separate destinations?**
- ✅ Different handling logic in client
- ✅ Type safety
- ✅ Performance (can unsubscribe if not needed)
- ✅ Clear separation of concerns

---

### 7. Method: `convertToWebSocketMessage()`

```java
public WebSocketNotificationMessage convertToWebSocketMessage(
    Notification notification, 
    String action
) {
    return WebSocketNotificationMessage.builder()
        .id(notification.getId())
        .title(notification.getTitle())
        .message(notification.getMessage())
        .type(notification.getType())
        .referenceId(notification.getReferenceId())
        .referenceType(notification.getReferenceType())
        .triggeredById(notification.getTriggeredBy() != null ? 
            notification.getTriggeredBy().getId() : null)
        .triggeredByName(notification.getTriggeredBy() != null ? 
            notification.getTriggeredBy().getDisplayName() : null)
        .timestamp(notification.getCreatedAt())
        .isRead(notification.getIsRead())
        .action(action)
        .build();
}
```

#### **Purpose: Entity → DTO Conversion**

**Why not send Entity directly?**

❌ **Problems with sending Entity:**
```java
// DON'T DO THIS
messagingTemplate.convertAndSendToUser(userId, dest, notification);
```

**Issues:**
1. **Lazy loading exception**
   ```java
   notification.getTriggeredBy().getName()
   // LazyInitializationException if session closed
   ```

2. **Circular references**
   ```java
   User → Notification → User → Notification → ...
   // Jackson can't serialize → StackOverflowError
   ```

3. **Too much data**
   ```java
   Notification has:
     - User (full object with all fields)
     - Project (full object)
     - Task (full object)
     - Comments (collection)
     - Attachments (collection)
   // Sends megabytes of unnecessary data
   ```

4. **Security risk**
   ```java
   User object contains:
     - password
     - email
     - private fields
   // Exposes sensitive data
   ```

✅ **Solution: Use DTO**
```java
WebSocketNotificationMessage = only necessary fields
```

#### **Null-safe field extraction:**

```java
.triggeredById(notification.getTriggeredBy() != null ? 
    notification.getTriggeredBy().getId() : null)
```

**Breakdown:**
```java
notification.getTriggeredBy()              // May be null
    ↓
if (triggeredBy != null)                   // Null check
    triggeredBy.getId()                    // Safe to call
else
    null                                   // Return null
```

**Without null check:**
```java
.triggeredById(notification.getTriggeredBy().getId())
// NullPointerException if triggeredBy is null!
```

**Modern Java alternative (Java 8+):**
```java
.triggeredById(
    Optional.ofNullable(notification.getTriggeredBy())
        .map(User::getId)
        .orElse(null)
)
```

**Even better with Optional:**
```java
.triggeredById(
    notification.getTriggeredByOptional()  // Returns Optional<User>
        .map(User::getId)
        .orElse(null)
)
```

#### **Field mapping:**

| Entity Field | DTO Field | Transformation |
|--------------|-----------|----------------|
| `notification.getId()` | `id` | Direct |
| `notification.getTitle()` | `title` | Direct |
| `notification.getMessage()` | `message` | Direct |
| `notification.getType()` | `type` | Direct (enum) |
| `notification.getTriggeredBy().getId()` | `triggeredById` | Null-safe extract |
| `notification.getTriggeredBy().getName()` | `triggeredByName` | Null-safe extract |
| `notification.getCreatedAt()` | `timestamp` | Direct (LocalDateTime) |
| `action` parameter | `action` | From parameter |

#### **Type conversions:**

**Enum serialization:**
```java
notification.getType()  // ENotificationType.TASK_ASSIGNMENT

// JSON:
"type": "TASK_ASSIGNMENT"  // String
```

**LocalDateTime serialization:**
```java
notification.getCreatedAt()  // LocalDateTime object

// JSON (ISO-8601):
"timestamp": "2025-11-16T10:30:00"
```

**Jackson configuration (auto by Spring):**
```java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
}
```

---

## 🔄 Complete Usage Flow

### Example: Task Assignment Notification

**Step 1: Create notification in database**
```java
@Service
public class TaskService {
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private WebSocketNotificationService webSocketService;
    
    public void assignTask(Long taskId, Long userId) {
        // ... assign task logic ...
        
        // Create notification entity
        Notification notification = Notification.builder()
            .user(user)
            .title("New Task Assigned")
            .message("You have been assigned to task: " + task.getTitle())
            .type(ENotificationType.TASK_ASSIGNMENT)
            .referenceId(taskId)
            .referenceType("TASK")
            .triggeredBy(currentUser)
            .isRead(false)
            .build();
        
        notification = notificationRepository.save(notification);
```

**Step 2: Convert to DTO**
```java
        // Convert entity to WebSocket DTO
        WebSocketNotificationMessage wsMessage = 
            webSocketService.convertToWebSocketMessage(
                notification,
                "NEW_NOTIFICATION"
            );
```

**Step 3: Send via WebSocket**
```java
        // Send to user
        webSocketService.sendNotificationToUser(userId, wsMessage);
        
        // Update count
        Long unreadCount = notificationRepository
            .countByUserAndIsRead(user, false);
        webSocketService.sendNotificationCount(userId, unreadCount);
    }
}
```

**Complete flow:**
```
TaskService.assignTask()
  │
  ├─> Save Notification to DB
  │     └─> notification (entity)
  │
  ├─> convertToWebSocketMessage()
  │     └─> wsMessage (DTO)
  │
  ├─> sendNotificationToUser()
  │     ├─> SimpMessagingTemplate
  │     ├─> Message Broker
  │     └─> WebSocket → Client
  │
  └─> sendNotificationCount()
        ├─> Count unread
        └─> WebSocket → Client
```

---

## 🧪 Testing

### Unit Test

```java
@ExtendWith(MockitoExtension.class)
class WebSocketNotificationServiceTest {
    
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    
    @InjectMocks
    private WebSocketNotificationService service;
    
    @Test
    void shouldSendNotificationToUser() {
        // Given
        Long userId = 123L;
        WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
            .title("Test")
            .build();
        
        // When
        service.sendNotificationToUser(userId, message);
        
        // Then
        verify(messagingTemplate).convertAndSendToUser(
            eq("123"),
            eq("/queue/notifications"),
            eq(message)
        );
    }
}
```

---

## 📊 Performance Considerations

### Message Size

**Entity (DON'T send):**
```json
{
  "id": 123,
  "title": "...",
  "user": {
    "id": 1,
    "username": "...",
    "email": "...",
    "password": "...",  // 😱 Security risk!
    "projects": [...],   // Huge nested data
    "notifications": [...],  // Circular reference
    ...
  },
  ...
}
// Size: ~50KB+
```

**DTO (DO send):**
```json
{
  "id": 123,
  "title": "New Task",
  "message": "...",
  "type": "TASK_ASSIGNMENT",
  "triggeredById": 1,
  "triggeredByName": "John Doe",
  "action": "NEW_NOTIFICATION"
}
// Size: ~500 bytes
```

**Performance impact:**
- DTO: **100x smaller**
- Network: **100x faster**
- Parsing: **100x faster**
- Memory: **100x less**

---

## 🔒 Security

### Best Practices

1. **Never send sensitive data**
```java
// ❌ DON'T
.password(user.getPassword())
.email(user.getEmail())

// ✅ DO
.userId(user.getId())
.userName(user.getDisplayName())
```

2. **Validate user before sending**
```java
public void sendNotificationToUser(Long userId, ...) {
    // Verify user exists
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());
    
    // Verify user is active
    if (!user.isActive()) {
        return;
    }
    
    // Then send
    messagingTemplate.convertAndSendToUser(...);
}
```

3. **Rate limiting**
```java
private final RateLimiter rateLimiter = RateLimiter.create(10.0); // 10/sec

public void sendNotificationToUser(Long userId, ...) {
    if (!rateLimiter.tryAcquire()) {
        log.warn("Rate limit exceeded for user {}", userId);
        return;
    }
    // Send...
}
```

---

## 📚 Summary

**WebSocketNotificationService responsibilities:**

1. ✅ **Send user-specific notifications**
2. ✅ **Broadcast system-wide notifications**
3. ✅ **Send real-time count updates**
4. ✅ **Notify status changes (read/delete)**
5. ✅ **Convert Entity → DTO safely**
6. ✅ **Handle errors gracefully**
7. ✅ **Log all operations**

**Key principles:**
- Use DTO, not Entity
- Null-safe field extraction
- Separate destinations for different message types
- Graceful error handling
- Thread-safe operations
