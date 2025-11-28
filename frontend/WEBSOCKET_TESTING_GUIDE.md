# Testing WebSocket Frontend - Hướng Dẫn Chi Tiết

## 🧪 Cách Test WebSocket ở Frontend

### **Phương Án 1: Sử dụng Test Component (Đơn Giản)**

#### Bước 1: Thêm Route Cho Test Component

**File: `src/App.tsx` hoặc router config**

```tsx
import { WebSocketTest } from '@/components/WebSocketTest';

// Thêm route
<Route path="/websocket-test" element={<WebSocketTest />} />
```

#### Bước 2: Cập Nhật User ID và Token

**File: `src/components/WebSocketTest.tsx`**

```typescript
// Line 9-10
const [userId, setUserId] = useState<number>(123); // Thay bằng userId thật của bạn
const [token, setToken] = useState<string>('your-jwt-token-here'); // JWT token từ login
```

**Lấy token từ localStorage (nếu bạn lưu sau khi login):**

```typescript
const [token, setToken] = useState<string>(
  localStorage.getItem('token') || ''
);
```

#### Bước 3: Truy Cập Test Page

```
http://localhost:5173/websocket-test
```

#### Bước 4: Kiểm Tra

✅ **Connection Status** phải hiện **🟢 CONNECTED**
✅ Mở **F12 → Console** để xem logs
✅ Mở **F12 → Network → WS** để xem WebSocket connection

---

### **Phương Án 2: Test Trong Browser Console**

#### Bước 1: Mở Browser Console (F12)

#### Bước 2: Import và Test

```javascript
// Vì service đã export, bạn có thể test trực tiếp
import { webSocketService } from './services/websocket.service';

// Connect
webSocketService.connect(123, 'your-token');

// Register callback để log
webSocketService.onNotification((notification) => {
  console.log('📬 Received notification:', notification);
});

webSocketService.onNotificationCount((count) => {
  console.log('🔢 Count update:', count);
});

webSocketService.onConnectionChange((connected) => {
  console.log('🔌 Connection status:', connected);
});
```

---

### **Phương Án 3: Check Network Tab**

#### Bước 1: Mở DevTools

**F12 → Network Tab → WS (WebSocket)**

#### Bước 2: Reload Page

Refresh page để capture WebSocket connection

#### Bước 3: Kiểm Tra

✅ **Name**: `ws` (endpoint của bạn)
✅ **Status**: `101 Switching Protocols`
✅ **Type**: `websocket`

#### Bước 4: Click Vào Connection

Xem **Messages** tab để thấy:
- **CONNECT** frame (màu đỏ - outgoing)
- **CONNECTED** frame (màu xanh - incoming)
- **SUBSCRIBE** frames (outgoing)
- **MESSAGE** frames (incoming - khi có notification)

---

### **Phương Án 4: Test Với Backend Debug Endpoint**

Tạo debug endpoint ở backend để gửi test notification:

**Backend:**

```java
@RestController
@RequestMapping("/api/debug")
public class WebSocketDebugController {
    
    @Autowired
    private WebSocketNotificationService webSocketService;
    
    @PostMapping("/test-notification/{userId}")
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) {
        WebSocketNotificationMessage msg = WebSocketNotificationMessage.builder()
            .id(999L)
            .title("Test Notification")
            .message("This is a test message from debug endpoint")
            .type("INFO")
            .action("NEW_NOTIFICATION")
            .timestamp(LocalDateTime.now().toString())
            .build();
            
        webSocketService.sendNotificationToUser(userId, msg);
        
        return ResponseEntity.ok("Test notification sent to user " + userId);
    }
}
```

**Test:**

```bash
# Gửi test notification
curl -X POST http://localhost:9090/api/debug/test-notification/123

# Frontend sẽ nhận notification ✅
```

---

### **Phương Án 5: Test Với Postman/Thunder Client**

#### Không thể test WebSocket trực tiếp với Postman

WebSocket cần browser environment. Nhưng bạn có thể:

1. **Test HTTP endpoints** (trigger backend gửi WebSocket)
2. **Dùng WebSocket testing tools**

---

### **Phương Án 6: Dùng WebSocket Testing Tools**

#### Option 1: **Firecamp** (Web-based)

1. Vào https://firecamp.dev/websocket
2. URL: `ws://localhost:9090/ws`
3. Protocol: `stomp`
4. Connect

#### Option 2: **wscat** (CLI)

```bash
# Install
npm install -g wscat

# Connect
wscat -c ws://localhost:9090/ws

# Send STOMP CONNECT
> CONNECT
> Authorization:Bearer your-token
> 
> ^@

# Send STOMP SUBSCRIBE
> SUBSCRIBE
> id:sub-0
> destination:/user/queue/notifications
> 
> ^@
```

---

## 🔍 Step-by-Step Testing Guide

### **Test 1: Connection**

```typescript
// 1. Mở test page
http://localhost:5173/websocket-test

// 2. Kiểm tra console logs
[WebSocket Debug] CONNECT
Authorization:Bearer xxx

[WebSocket Debug] CONNECTED
version:1.2
session:abc-123

// 3. Kiểm tra UI
Status: 🟢 CONNECTED ✅
```

---

### **Test 2: Subscriptions**

```typescript
// Trong console sẽ thấy:
[WebSocket Debug] SUBSCRIBE
id:sub-0
destination:/user/queue/notifications

[WebSocket Debug] SUBSCRIBE
id:sub-1
destination:/user/queue/notification-count

[WebSocket Debug] SUBSCRIBE
id:sub-2
destination:/user/queue/notification-updates

[WebSocket Debug] SUBSCRIBE
id:sub-3
destination:/topic/notifications

Subscribed to notification topics for user 123 ✅
```

---

### **Test 3: Receive Notification**

#### Backend gửi notification:

```java
// Ví dụ: Assign task to user 123
notificationService.createNotification(userId=123, "New Task Assigned");
```

#### Frontend console logs:

```typescript
// 1. Service receives
[WebSocket Debug] MESSAGE
destination:/user/queue/notifications
message-id:007

Received notification: {
  id: 1,
  title: "New Task Assigned",
  message: "You have been assigned to task #45",
  type: "TASK_ASSIGNED",
  action: "NEW_NOTIFICATION"
}

// 2. Hook receives
Hook received notification: { ... }

// 3. State updated
// UI shows new notification ✅
```

---

### **Test 4: Browser Notification**

```typescript
// 1. Grant permission
Notification.requestPermission() → Allow

// 2. Khi notification đến
// Browser notification popup xuất hiện ✅
// Title: "New Task Assigned"
// Body: "You have been assigned to task #45"
```

---

### **Test 5: Reconnection**

```typescript
// 1. Stop backend server
// Console logs:
WebSocket disconnected
Attempting to reconnect (1/5)...

// 2. After 3 seconds
Connecting to WebSocket: http://localhost:9090/ws
WebSocket error: ...

// 3. Keeps retrying
Attempting to reconnect (2/5)...
Attempting to reconnect (3/5)...

// 4. Restart backend
// Reconnects automatically ✅
WebSocket connected successfully
```

---

## 📋 Testing Checklist

### Frontend Checklist

- [ ] **Connection Status**
  - [ ] Shows "CONNECTED" when online
  - [ ] Shows "DISCONNECTED" when offline
  
- [ ] **Subscriptions**
  - [ ] Subscribe to `/user/queue/notifications`
  - [ ] Subscribe to `/user/queue/notification-count`
  - [ ] Subscribe to `/user/queue/notification-updates`
  - [ ] Subscribe to `/topic/notifications`

- [ ] **Receive Messages**
  - [ ] New notification appears in UI
  - [ ] Count updates correctly
  - [ ] Read/Delete actions work

- [ ] **Browser Notifications**
  - [ ] Permission requested
  - [ ] Popup shows on new notification

- [ ] **Cleanup**
  - [ ] Disconnects on component unmount
  - [ ] No memory leaks
  - [ ] No React warnings

---

### Backend Checklist

- [ ] **Endpoint Accessible**
  - [ ] `ws://localhost:9090/ws` responds
  - [ ] CORS configured correctly

- [ ] **Authentication**
  - [ ] JWT token validated
  - [ ] Principal set correctly

- [ ] **Message Sending**
  - [ ] `sendNotificationToUser()` works
  - [ ] Message reaches correct user
  - [ ] Multi-tab delivery works

---

## 🐛 Common Issues

### Issue 1: Connection Refused

**Error:**
```
WebSocket connection failed: net::ERR_CONNECTION_REFUSED
```

**Solution:**
- ✅ Check backend is running on port 9090
- ✅ Check `.env` file: `VITE_WS_URL=http://localhost:9090/ws`

---

### Issue 2: Status Always DISCONNECTED

**Error:**
```
Status: 🔴 DISCONNECTED
```

**Check:**
1. Backend running? → `curl http://localhost:9090/actuator/health`
2. CORS configured? → Check `WebSocketConfig.java`
3. Token valid? → Check JWT not expired

---

### Issue 3: No Messages Received

**Error:**
```
Subscribed but no messages appear
```

**Check:**
1. Backend sending? → Add logs in `WebSocketNotificationService`
2. Correct userId? → Check userId matches
3. Subscription active? → Check Network tab → WS → Messages

---

### Issue 4: Browser Notification Not Showing

**Error:**
```
Notification doesn't popup
```

**Solution:**
1. Check permission: `Notification.permission` → should be "granted"
2. Re-request permission if denied
3. Check browser settings (notifications allowed?)

---

## 💡 Quick Test Script

Paste vào **Browser Console** để test nhanh:

```javascript
// Test connection status
console.log('Connected:', webSocketService.getConnectionStatus());

// Send test message (if you have bidirectional communication)
webSocketService.send('/app/test', { message: 'Hello from console!' });

// Check localStorage for token
console.log('Token:', localStorage.getItem('token'));

// Request notification permission
Notification.requestPermission().then(permission => {
  console.log('Notification permission:', permission);
});
```

---

## 📊 Success Criteria

✅ **Connection established** (green status)
✅ **Console shows CONNECT, CONNECTED, SUBSCRIBE frames**
✅ **Network tab shows WS connection with status 101**
✅ **When backend sends notification, UI updates immediately**
✅ **Browser notification pops up**
✅ **Count updates correctly**
✅ **Reconnects automatically on disconnect**
✅ **No errors in console**
✅ **No React warnings about memory leaks**

---

## 🚀 Next Steps After Testing

1. **Remove Test Component** - Sau khi test xong
2. **Integrate into Real Components** - Dùng hook trong NotificationList, Header, etc.
3. **Handle Error Cases** - Add retry logic, error messages
4. **Add Loading States** - Show loading when connecting
5. **Optimize Performance** - Limit notification array size

---

**Bắt đầu test ngay với `/websocket-test` route! 🧪**
