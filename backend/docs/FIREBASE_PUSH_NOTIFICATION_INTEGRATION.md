# Firebase Push Notification Integration

**Date:** November 29, 2025  
**Status:** ✅ Completed  
**Version:** 1.0

---

## Overview

Firebase Cloud Messaging (FCM) đã được tích hợp vào hệ thống notification để đảm bảo users nhận được thông báo ngay cả khi:
- ❌ App/Web bị đóng
- ❌ User offline
- ❌ Browser không mở

## Architecture

### Dual Notification System

```
┌─────────────────────────────────────────────────────────────┐
│                  NotificationHelperService                   │
│                  createNotification()                        │
└───────────────────────┬─────────────────────────────────────┘
                        │
        ┌───────────────┴────────────────┐
        │                                │
        ▼                                ▼
┌──────────────────┐          ┌──────────────────────┐
│   WebSocket      │          │  Firebase Push       │
│   (Real-time)    │          │  (Persistent)        │
└──────────────────┘          └──────────────────────┘
        │                                │
        │ User ONLINE                    │ User OFFLINE
        │ App đang mở                    │ App đã đóng
        ▼                                ▼
┌──────────────────┐          ┌──────────────────────┐
│  Browser tab     │          │  System notification │
│  In-app toast    │          │  Push badge          │
└──────────────────┘          └──────────────────────┘
```

---

## Implementation Details

### 1. NotificationHelperService.java

#### Method: `createNotification()`

```java
public Notification createNotification(...) {
    // 1. Save to database
    notification = notificationRepository.save(notification);
    
    // 2. ✅ Send WebSocket (for online users)
    try {
        webSocketNotificationService.sendNotificationToUser(user.getId(), wsMessage);
        webSocketNotificationService.sendNotificationCount(user.getId(), unreadCount);
    } catch (Exception e) {
        log.error("Failed to send WebSocket notification");
    }

    // 3. ✅ Send Firebase Push (for offline users)
    try {
        sendFirebasePushNotification(user, notification, title, message, type, referenceId);
    } catch (Exception e) {
        log.error("Failed to send Firebase push notification");
    }

    return notification;
}
```

#### Method: `sendFirebasePushNotification()`

**Purpose:** Gửi push notification qua Firebase Cloud Messaging

**Flow:**
1. Lấy tất cả active device tokens của user từ `UserDeviceTokenRepository`
2. Kiểm tra có tokens không → nếu không có thì skip
3. Build `PushNotificationRequest` với:
   - `title`: Tiêu đề notification
   - `body`: Nội dung message
   - `data`: Custom data (notificationId, type, referenceId, etc.)
4. Gọi `FirebaseMessagingService.sendMulticastNotification()` để gửi đến nhiều devices
5. Log kết quả (success/failure)

**Code:**
```java
private void sendFirebasePushNotification(...) {
    // Get active device tokens
    List<UserDeviceToken> deviceTokens = userDeviceTokenRepository
        .findByUserAndIsActiveTrue(user);
    
    if (deviceTokens.isEmpty()) {
        return; // No devices to send to
    }

    // Extract FCM tokens
    List<String> fcmTokens = deviceTokens.stream()
        .map(UserDeviceToken::getFcmToken)
        .collect(Collectors.toList());

    // Prepare data payload
    Map<String, String> data = new HashMap<>();
    data.put("notificationId", notification.getId().toString());
    data.put("type", type.name());
    data.put("referenceId", referenceId.toString());
    // ... more data fields

    // Build request
    PushNotificationRequest pushRequest = PushNotificationRequest.builder()
        .title(title)
        .body(message)
        .data(data)
        .build();

    // Send via Firebase
    firebaseMessagingService.sendMulticastNotification(pushRequest, fcmTokens);
}
```

---

## Data Flow

### Notification Creation Flow

```
1. User Action (e.g., Task Assigned)
   │
   ▼
2. Service Layer (e.g., TaskServiceImpl)
   │
   └─→ notificationHelperService.createNotificationsForUsers()
       │
       ▼
3. NotificationHelperService.createNotification()
   │
   ├─→ Save to Database (notifications table)
   │
   ├─→ Send WebSocket
   │   └─→ /user/{userId}/queue/notifications
   │
   └─→ Send Firebase Push
       │
       ├─→ Query UserDeviceToken table
       ├─→ Get FCM tokens
       └─→ FirebaseMessagingService.sendMulticastNotification()
           │
           └─→ Firebase Cloud Messaging API
               │
               └─→ User's device(s)
```

### Notification Payload

**WebSocket Payload:**
```json
{
  "id": 123,
  "title": "Task Assigned",
  "message": "John assigned you to Task ABC",
  "type": "TASK_ASSIGNED",
  "isRead": false,
  "createdDate": "2025-11-29T10:30:00",
  "link": "/projects/1/tasks/5"
}
```

**Firebase Push Payload:**
```json
{
  "notification": {
    "title": "Task Assigned",
    "body": "John assigned you to Task ABC"
  },
  "data": {
    "notificationId": "123",
    "type": "TASK_ASSIGNED",
    "referenceId": "5",
    "referenceType": "TASK",
    "userId": "10",
    "timestamp": "2025-11-29T10:30:00"
  }
}
```

---

## Dependencies

### Backend Dependencies

**Spring Boot:**
- `NotificationRepository` - JPA repository for Notification entity
- `UserDeviceTokenRepository` - JPA repository for device tokens
- `FirebaseMessagingService` - Service gửi FCM notifications
- `WebSocketNotificationService` - Service gửi WebSocket messages

**Firebase:**
- `com.google.firebase:firebase-admin` - Firebase Admin SDK
- `FirebaseMessaging` - FCM API

### Database Tables

**notifications:**
- Lưu trữ tất cả notifications
- Liên kết với User, Project

**user_device_tokens:**
- Lưu FCM tokens của users
- Fields: `user_id`, `fcm_token`, `device_type`, `is_active`

---

## Configuration

### Required Services

1. **FirebaseConfig.java** ✅
   - Initialize Firebase Admin SDK
   - Load service account credentials

2. **FirebaseMessagingService.java** ✅
   - `sendNotificationToToken()` - Gửi đến 1 device
   - `sendMulticastNotification()` - Gửi đến nhiều devices
   - `sendNotificationToTopic()` - Gửi đến topic

3. **UserDeviceTokenRepository.java** ✅
   - `findByUser()` - Get all tokens của user
   - `findByUserAndIsActiveTrue()` - Get active tokens
   - `findByFcmToken()` - Find by FCM token

---

## Usage Examples

### 1. Task Assignment Notification

```java
// In TaskServiceImpl.assignTask()
notificationHelperService.createNotificationsForUsers(
    assignees,                           // List<User>
    "Task Assigned",                     // title
    "You have been assigned to task",   // message
    ENotificationType.TASK_ASSIGNED,     // type
    task.getId(),                        // referenceId
    "TASK",                              // referenceType
    currentUser                          // triggeredBy
);
```

**Result:**
- ✅ Notification saved to database
- ✅ WebSocket message sent (if user online)
- ✅ Firebase push sent to all user's devices

### 2. Comment Notification

```java
// In CommentServiceImpl.createComment()
notificationHelperService.createNotificationsForAllProjectMembers(
    project,
    "New Comment",
    "John commented on report",
    ENotificationType.COMMENT_ADDED,
    comment.getId(),
    "COMMENT",
    author
);
```

### 3. Deadline Warning

```java
// In DeadlineNotificationScheduler (cron job)
notificationHelperService.createNotificationsForAllProjectMembers(
    project,
    "Deadline Approaching",
    "Project deadline is in 3 days",
    ENotificationType.DEADLINE_APPROACHING,
    project.getId(),
    "PROJECT",
    null
);
```

---

## Error Handling

### WebSocket Failures

- **Non-blocking**: WebSocket errors không làm fail toàn bộ notification
- Wrapped in try-catch
- Log error nhưng vẫn tiếp tục gửi Firebase push

### Firebase Push Failures

- **Non-blocking**: Push errors không làm fail notification creation
- Wrapped in try-catch
- Invalid tokens có thể được cleanup (TODO: implement `handleInvalidTokens()`)

### Invalid FCM Tokens

**Scenarios:**
1. Token expired
2. User uninstalled app
3. Token revoked

**Handling:**
```java
private void handleInvalidTokens(Exception exception, List<String> tokens) {
    // TODO: Parse BatchResponse to find invalid tokens
    // Remove invalid tokens from database
    // FirebaseMessaging returns error codes for each token
}
```

---

## Frontend Integration

### Requirements

1. **Register Device Token**
   ```typescript
   // POST /api/v1/device-tokens/register
   {
     "fcmToken": "firebase-token-here",
     "deviceType": "WEB",
     "deviceInfo": navigator.userAgent
   }
   ```

2. **Firebase Service Worker**
   - `public/firebase-messaging-sw.js`
   - Handle background notifications
   - Display system notifications

3. **Request Notification Permission**
   ```javascript
   const permission = await Notification.requestPermission();
   if (permission === 'granted') {
     const token = await messaging.getToken();
     await registerDeviceToken(token);
   }
   ```

---

## Testing

### Manual Testing

1. **Test WebSocket (Online User):**
   - Login user A
   - Trigger notification (e.g., assign task)
   - Verify toast notification appears in browser

2. **Test Firebase Push (Offline User):**
   - Register device token for user B
   - Close browser tab
   - Trigger notification for user B
   - Verify system notification appears

3. **Test Multiple Devices:**
   - Register 2+ device tokens for user C
   - Trigger notification
   - Verify all devices receive push

### API Endpoints for Testing

**Send Test Notification:**
```bash
POST /api/v1/device-tokens/test-notification
Authorization: Bearer {token}
```

**Get My Device Tokens:**
```bash
GET /api/v1/device-tokens/my-tokens
Authorization: Bearer {token}
```

---

## Performance Considerations

### Batch Sending

- Use `sendMulticastNotification()` instead of sending individually
- Firebase allows up to 500 tokens per multicast
- Batch requests reduce API calls

### Async Processing

- Firebase calls are async (`.sendAsync()`)
- Non-blocking - doesn't delay main request
- Failures don't affect user experience

### Token Management

- Clean up expired/invalid tokens
- Set `is_active = false` for failed tokens
- Periodic cleanup job recommended

---

## Security

### Token Protection

- Store FCM tokens in database (encrypted if needed)
- Validate user owns device token before operations
- Delete tokens on logout

### Data Privacy

- Don't send sensitive data in notification body
- Use data payload for IDs only
- Fetch full data from API when notification clicked

---

## Monitoring & Logging

### Log Levels

**INFO:**
- ✅ Sent WebSocket notification to user X
- ✅ Sent Firebase push notification to N device(s)

**ERROR:**
- ❌ Failed to send WebSocket notification
- ❌ Failed to send Firebase push notification

**DEBUG:**
- No active device tokens found for user X
- Handling invalid tokens (if any)

### Metrics to Track

1. **Notification Delivery Rate**
   - WebSocket success rate
   - Firebase push success rate

2. **Device Token Health**
   - Active tokens count
   - Invalid tokens count
   - Token registration rate

3. **Notification Types**
   - Count by notification type
   - Most frequent notification triggers

---

## Future Enhancements

### 1. Token Cleanup
```java
// Implement automatic cleanup of invalid tokens
private void handleInvalidTokens(BatchResponse response) {
    for (int i = 0; i < response.getResponses().size(); i++) {
        SendResponse sendResponse = response.getResponses().get(i);
        if (!sendResponse.isSuccessful()) {
            String token = fcmTokens.get(i);
            // Check error code
            if (isInvalidToken(sendResponse.getException())) {
                userDeviceTokenRepository.deleteByFcmToken(token);
            }
        }
    }
}
```

### 2. Topic-based Notifications
```java
// Subscribe all project members to project topic
firebaseMessagingService.subscribeToTopic(token, "project-" + projectId);

// Send to topic instead of individual tokens
firebaseMessagingService.sendNotificationToTopic(request);
```

### 3. Notification Preferences
```java
// Allow users to configure notification preferences
userPreferences.setEmailNotifications(true);
userPreferences.setPushNotifications(false);
userPreferences.setWebSocketNotifications(true);
```

### 4. Rich Notifications
```java
// Add images, actions, custom sounds
pushRequest.setImageUrl("https://example.com/image.png");
pushRequest.setActions([
    { title: "View", action: "view" },
    { title: "Dismiss", action: "dismiss" }
]);
```

---

## Troubleshooting

### Issue: No Firebase push received

**Checklist:**
1. ✅ User has registered device token?
   - Check `user_device_tokens` table
2. ✅ Token is active?
   - `is_active = true`
3. ✅ Firebase credentials configured?
   - Check `FirebaseConfig.java`
4. ✅ Service worker registered? (Web only)
   - Check browser console
5. ✅ Notification permission granted?
   - Check browser settings

### Issue: Push received but WebSocket not

**Checklist:**
1. ✅ User is online?
2. ✅ WebSocket connection established?
   - Check browser Network tab → WS
3. ✅ Subscribed to correct topic?
   - `/user/{userId}/queue/notifications`

### Issue: Multiple duplicate notifications

**Possible causes:**
1. Multiple device tokens registered
   - Expected behavior - one per device
2. Multiple WebSocket connections
   - Bug - should be single connection per session
3. Notification created multiple times
   - Check service layer logic

---

## References

- [Firebase Admin SDK Documentation](https://firebase.google.com/docs/admin/setup)
- [FCM HTTP v1 API](https://firebase.google.com/docs/cloud-messaging/http-server-ref)
- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)

---

## Summary

| Feature | Status | Notes |
|---------|--------|-------|
| WebSocket Notifications | ✅ Complete | Real-time for online users |
| Firebase Push | ✅ Complete | Works offline & app closed |
| Database Storage | ✅ Complete | All notifications persisted |
| Multi-device Support | ✅ Complete | Multicast to all user devices |
| Error Handling | ✅ Complete | Non-blocking, logged errors |
| Token Management | ⚠️ Partial | Cleanup TODO |
| Topic Subscriptions | ❌ Not implemented | Future enhancement |

**Last Updated:** November 29, 2025  
**Author:** GitHub Copilot + Development Team
