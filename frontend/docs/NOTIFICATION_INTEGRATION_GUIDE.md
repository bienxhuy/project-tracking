# Frontend Notification System Integration Guide

**Date:** November 29, 2025  
**Status:** ✅ Completed  
**Version:** 1.0

---

## Overview

Hệ thống notification đã được tích hợp hoàn chỉnh vào frontend với 2 kênh:
1. **WebSocket** - Real-time notifications khi user đang online
2. **Firebase Push** - Background notifications khi app bị đóng hoặc user offline

---

## Implementation Changes

### 1. Login Flow với Notification Permission

**File:** `frontend/src/pages/LoginPage.tsx`

#### Thay đổi:
- ✅ Import `NotificationPermissionDialog` component
- ✅ Import Firebase services (`requestNotificationPermission`, `registerDeviceToken`)
- ✅ Thêm state `showNotificationDialog`
- ✅ Show permission dialog sau khi login thành công
- ✅ Register FCM token với backend sau khi user cho phép

#### Flow:
```
User Login Success
    │
    ▼
Check Notification Permission
    │
    ├─→ Already Granted → Navigate to Home
    │
    ├─→ Not Granted & Not Skipped → Show Permission Dialog
    │   │
    │   ├─→ User Clicks "Cho phép"
    │   │   ├─→ Request Browser Permission
    │   │   ├─→ Get FCM Token
    │   │   ├─→ Register with Backend
    │   │   └─→ Navigate to Home
    │   │
    │   └─→ User Clicks "Để sau"
    │       ├─→ Mark as skipped in localStorage
    │       └─→ Navigate to Home
    │
    └─→ Already Skipped → Navigate to Home
```

**Code Example:**
```typescript
// Check permission after login
useEffect(() => {
  if (isAuthenticated) {
    const permissionSkipped = localStorage.getItem('notificationPermissionSkipped');
    const currentPermission = Notification.permission;
    
    if (currentPermission !== 'granted' && !permissionSkipped) {
      setShowNotificationDialog(true);
    } else {
      navigate("/", { replace: true });
    }
  }
}, [isAuthenticated, navigate]);

// Handle permission request
const handleRequestPermission = async () => {
  const token = await requestNotificationPermission();
  
  if (token) {
    await registerDeviceToken(token, 'WEB');
    localStorage.setItem('fcmToken', token);
  }
  
  navigate("/", { replace: true });
};
```

---

### 2. Notification Permission Dialog Component

**File:** `frontend/src/components/NotificationPermissionDialog.tsx`

#### Features:
- ✅ Beautiful UI với icons và descriptions
- ✅ 4 benefit points được highlight
- ✅ 2 buttons: "Cho phép" và "Để sau"
- ✅ Loading state khi đang request permission
- ✅ Auto-close sau khi granted

#### UI Benefits Shown:
1. **Task được giao mới** - Nhận thông báo khi được giao nhiệm vụ
2. **Bình luận và mention** - Được thông báo khi có người nhắc đến bạn
3. **Cảnh báo deadline** - Nhắc nhở trước khi hết hạn nộp
4. **Cập nhật dự án** - Thông báo về các thay đổi trong dự án

---

### 3. WebSocket Notifications Hook

**File:** `frontend/src/hooks/useWebSocketNotifications.ts`

#### Purpose:
Custom React hook quản lý WebSocket connection và notifications

#### Features:
- ✅ Auto-connect khi user authenticated
- ✅ Subscribe to notification callbacks
- ✅ Handle incoming notifications (add to list + show toast)
- ✅ Track unread count
- ✅ Connection status monitoring
- ✅ Mark as read functionality
- ✅ Auto cleanup on unmount

#### Usage:
```typescript
const {
  notifications,      // Array of WebSocketNotification
  unreadCount,        // Number of unread notifications
  isConnected,        // WebSocket connection status
  markAsRead,         // Function to mark notification as read
  clearNotifications, // Function to clear all notifications
} = useWebSocketNotifications();
```

#### Notification Handling:
```typescript
const handleNotification = useCallback((notification: WebSocketNotification) => {
  // Add to notifications list
  setNotifications((prev) => [notification, ...prev]);

  // Show toast for new notifications
  if (notification.action === 'NEW_NOTIFICATION') {
    addToast({
      title: notification.title,
      description: notification.message,
      variant: 'default',
    });

    // Play notification sound (optional)
    const audio = new Audio('/notification-sound.mp3');
    audio.volume = 0.5;
    audio.play();
  }
}, [addToast]);
```

---

### 4. Header Component Integration

**File:** `frontend/src/components/Header.tsx`

#### Changes:
- ✅ Replace dummy data với WebSocket notifications
- ✅ Import `useWebSocketNotifications` hook
- ✅ Show unread count badge trên bell icon
- ✅ Real-time notification list updates
- ✅ Mark as read on click

#### UI Updates:
```tsx
{/* Bell icon with unread badge */}
<Button className="cursor-pointer relative" variant="ghost" size="icon">
  <Bell className="w-5 h-5" />
  {unreadCount > 0 && (
    <Badge 
      className="absolute -top-1 -right-1 h-5 w-5"
      variant="destructive"
    >
      {unreadCount > 99 ? '99+' : unreadCount}
    </Badge>
  )}
</Button>
```

#### Notification Display:
```tsx
{notifications.map((notification) => (
  <div
    key={notification.id}
    className={notification.isRead ? "bg-transparent" : "bg-blue-50"}
    onClick={() => handleNotificationClick(notification.id)}
  >
    <h3>{notification.title}</h3>
    <span>{new Date(notification.timestamp).toLocaleString()}</span>
    <p>{notification.message}</p>
  </div>
))}
```

---

## File Structure

```
frontend/src/
├── components/
│   ├── Header.tsx                           ✅ Updated - WebSocket integration
│   ├── NotificationPermissionDialog.tsx     ✅ New - Permission dialog
│   └── ui/
│       └── badge.tsx                        (Required for unread count)
│
├── hooks/
│   ├── useNotification.ts                   ✅ Existing - Firebase hook
│   └── useWebSocketNotifications.ts         ✅ New - WebSocket hook
│
├── pages/
│   └── LoginPage.tsx                        ✅ Updated - Permission flow
│
├── services/
│   ├── firebase.service.ts                  ✅ Existing - Firebase SDK
│   ├── notification.api.ts                  ✅ Existing - Backend API
│   └── websocket.service.ts                 ✅ Existing - WebSocket client
│
└── types/
    └── notification.type.ts                 (For type definitions)
```

---

## Data Flow

### Complete Notification Flow

```
Backend Event (e.g., Task Assignment)
    │
    ▼
NotificationHelperService.createNotification()
    │
    ├─→ Save to Database
    │
    ├─→ Send WebSocket
    │   └─→ WebSocketService (Backend)
    │       └─→ Send to /user/{userId}/queue/notifications
    │           └─→ SockJS Client (Frontend)
    │               └─→ useWebSocketNotifications Hook
    │                   ├─→ Add to notifications array
    │                   ├─→ Show Toast
    │                   ├─→ Play Sound
    │                   └─→ Update unread count
    │
    └─→ Send Firebase Push
        └─→ FirebaseMessagingService (Backend)
            └─→ Firebase Cloud Messaging API
                └─→ User's Browser/Device
                    ├─→ Foreground: onMessage listener
                    │   └─→ Show in-app notification
                    │
                    └─→ Background: Service Worker
                        └─→ Show system notification
```

---

## Testing

### Manual Testing Steps

#### 1. Test Login Flow
```bash
1. Logout (if logged in)
2. Login with valid credentials
3. ✅ Permission dialog should appear
4. Click "Cho phép"
5. ✅ Browser requests permission
6. ✅ After granted, navigate to home
7. Check localStorage: fcmToken should be set
8. Check backend: device token should be registered
```

#### 2. Test WebSocket Notifications
```bash
1. Login and ensure connection is established
2. Open browser console → Network → WS tab
3. ✅ Should see WebSocket connection
4. Trigger notification (e.g., have someone assign you a task)
5. ✅ Toast notification should appear
6. ✅ Header bell icon shows unread count badge
7. Click bell icon
8. ✅ Notification appears in dropdown list
9. Click notification
10. ✅ Notification marked as read (badge count decreases)
```

#### 3. Test Firebase Push (Background)
```bash
1. Login and grant notification permission
2. Close browser tab (or minimize)
3. Trigger notification from another user
4. ✅ System notification should appear
5. Click notification
6. ✅ Browser opens to the app
```

#### 4. Test "Để sau" Flow
```bash
1. Logout
2. Login
3. Click "Để sau" on permission dialog
4. ✅ Navigate to home without permission
5. Check localStorage: notificationPermissionSkipped = 'true'
6. Logout and login again
7. ✅ Permission dialog should NOT appear
```

---

## Troubleshooting

### Issue: "global is not defined" Error

**Error:**
```
Uncaught ReferenceError: global is not defined
    at node_modules/sockjs-client/lib/utils/browser-crypto.js
```

**Solution:**
This happens because SockJS client requires the `global` variable which Vite doesn't polyfill by default.

**Fix in `vite.config.ts`:**
```typescript
export default defineConfig({
  // ... other config
  define: {
    global: 'globalThis',
  },
})
```

**After fix:**
1. ✅ Save vite.config.ts
2. ✅ Restart dev server: `npm run dev`
3. ✅ Clear browser cache
4. ✅ Reload page

---

### Issue: Permission Dialog không hiện

**Checklist:**
1. ✅ Clear localStorage: `localStorage.clear()`
2. ✅ Reset browser notification permission
3. ✅ Logout and login again
4. ✅ Check console for errors

### Issue: WebSocket không kết nối

**Checklist:**
1. ✅ Check VITE_WS_URL in `.env`
2. ✅ Backend WebSocket endpoint running?
3. ✅ JWT token valid?
4. ✅ Check Network tab → WS connection
5. ✅ Check backend logs

### Issue: Firebase Push không nhận

**Checklist:**
1. ✅ Permission granted?
2. ✅ FCM token registered with backend?
3. ✅ Service worker registered?
4. ✅ Firebase config correct in `.env`?
5. ✅ Check browser console for Firebase errors

### Issue: Unread count không update

**Checklist:**
1. ✅ WebSocket connected?
2. ✅ Subscribe to `/user/{userId}/queue/notification-count`?
3. ✅ Backend sending count updates?
4. ✅ Check `useWebSocketNotifications` hook

---

## Environment Variables

### Required in `.env`:

```bash
# API Base URL
VITE_API_BASE_URL=http://localhost:9090

# WebSocket URL
VITE_WS_URL=http://localhost:9090/ws

# Firebase Configuration
VITE_FIREBASE_API_KEY=your_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id
VITE_FIREBASE_VAPID_KEY=your_vapid_key
```

---

## Browser Compatibility

| Feature | Chrome | Firefox | Safari | Edge |
|---------|--------|---------|--------|------|
| WebSocket | ✅ | ✅ | ✅ | ✅ |
| Push Notifications | ✅ | ✅ | ⚠️ Limited | ✅ |
| Service Worker | ✅ | ✅ | ✅ | ✅ |

**Note:** Safari on iOS has limited push notification support. Test thoroughly on target browsers.

---

## Future Enhancements

### 1. Notification Preferences
```typescript
// Allow users to configure notification types
interface NotificationPreferences {
  taskAssigned: boolean;
  comments: boolean;
  mentions: boolean;
  deadlineWarnings: boolean;
  projectUpdates: boolean;
}
```

### 2. Notification Actions
```typescript
// Add action buttons to notifications
{
  title: "New Task Assigned",
  actions: [
    { title: "View Task", action: "view" },
    { title: "Dismiss", action: "dismiss" }
  ]
}
```

### 3. Notification Grouping
- Group notifications by project
- Collapse similar notifications
- Batch notifications

### 4. Rich Notifications
- Add images/avatars
- Custom sounds per notification type
- Priority levels

---

## API Integration

### Register Device Token
```typescript
POST /api/v1/device-tokens/register
Authorization: Bearer {token}

{
  "fcmToken": "firebase-token-here",
  "deviceType": "WEB",
  "deviceInfo": "Mozilla/5.0..."
}
```

### Get Notifications
```typescript
GET /api/v1/notifications
Authorization: Bearer {token}

Response:
{
  "status": 200,
  "data": {
    "totalCount": 10,
    "projectNotifications": [...]
  }
}
```

### Mark as Read
```typescript
PATCH /api/v1/notifications/{id}/read
Authorization: Bearer {token}
```

---

## Summary

| Component | Status | Description |
|-----------|--------|-------------|
| **LoginPage** | ✅ Complete | Permission dialog after login |
| **NotificationPermissionDialog** | ✅ Complete | Beautiful permission UI |
| **useWebSocketNotifications** | ✅ Complete | WebSocket hook |
| **Header** | ✅ Complete | Real-time notifications display |
| **Firebase Integration** | ✅ Complete | Background push notifications |
| **WebSocket Connection** | ✅ Complete | Real-time updates |
| **Unread Badge** | ✅ Complete | Visual indicator |
| **Toast Notifications** | ✅ Complete | In-app alerts |

---

## Next Steps

1. ✅ Test end-to-end flow
2. ✅ Add notification sound file (`/public/notification-sound.mp3`)
3. ⚠️ Update .env with real Firebase credentials
4. ⚠️ Test on multiple browsers
5. ⚠️ Implement mark all as read
6. ⚠️ Add notification filtering by project
7. ⚠️ Implement notification preferences

---

**Last Updated:** November 29, 2025  
**Author:** GitHub Copilot + Development Team
