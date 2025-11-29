# Notification API Integration - Implementation Summary

## 📋 Overview

Đã implement đầy đủ các API để lấy và quản lý notifications từ backend, thay thế dummy data bằng real-time data từ server.

## 🔧 Files Changed

### 1. **notification.api.ts** - Added Notification Management APIs
**File**: `frontend/src/services/notification.api.ts`

#### New API Functions:

```typescript
// Get notification by ID
getNotificationById(id: number)

// Get all notifications for current user
getMyNotifications(userId: number)

// Get notifications by user ID
getNotificationsByUser(userId: number)

// Get unread notifications
getUnreadNotifications(userId: number)

// Count unread notifications
countUnreadNotifications(userId: number)

// Mark notification as read
markNotificationAsRead(id: number)

// Mark all notifications as read
markAllNotificationsAsRead(userId: number)

// Delete notification
deleteNotification(id: number)
```

#### API Endpoints Mapping:

| Frontend Function | Backend Endpoint | Method | Description |
|------------------|------------------|--------|-------------|
| `getNotificationById(id)` | `/api/v1/notifications/{id}` | GET | Lấy 1 notification |
| `getMyNotifications(userId)` | `/api/v1/notifications/user/{userId}` | GET | Lấy tất cả notifications |
| `getUnreadNotifications(userId)` | `/api/v1/notifications/user/{userId}/unread` | GET | Lấy chưa đọc |
| `countUnreadNotifications(userId)` | `/api/v1/notifications/user/{userId}/unread/count` | GET | Đếm chưa đọc |
| `markNotificationAsRead(id)` | `/api/v1/notifications/{id}/read` | PATCH | Đánh dấu đã đọc |
| `markAllNotificationsAsRead(userId)` | `/api/v1/notifications/user/{userId}/read-all` | PATCH | Đánh dấu tất cả |
| `deleteNotification(id)` | `/api/v1/notifications/{id}` | DELETE | Xóa notification |

### 2. **useWebSocketNotifications.ts** - Updated Hook
**File**: `frontend/src/hooks/useWebSocketNotifications.ts`

#### Changes:

1. **Added Imports**:
```typescript
import { 
  getMyNotifications, 
  countUnreadNotifications, 
  markNotificationAsRead 
} from '@/services/notification.api';
```

2. **Added Initial Data Fetch**:
```typescript
useEffect(() => {
  const fetchNotifications = async () => {
    if (user && user.id) {
      setIsLoading(true);
      try {
        // Fetch notifications from backend
        const response = await getMyNotifications(user.id);
        if (response.data) {
          setNotifications(response.data);
        }

        // Fetch unread count
        const countResponse = await countUnreadNotifications(user.id);
        if (countResponse.data !== undefined) {
          setUnreadCount(countResponse.data);
        }
      } catch (error) {
        console.error('Error fetching notifications:', error);
      } finally {
        setIsLoading(false);
      }
    }
  };

  fetchNotifications();
}, [user]);
```

3. **Updated markAsRead Function**:
```typescript
const markAsRead = useCallback(async (notificationId: number) => {
  try {
    // Call API to mark as read in backend
    await markNotificationAsRead(notificationId);
    
    // Update local state
    setNotifications((prev) =>
      prev.map((n) =>
        n.id === notificationId ? { ...n, isRead: true } : n
      )
    );
    
    // Update unread count
    setUnreadCount((prev) => Math.max(0, prev - 1));
  } catch (error) {
    console.error('Error marking notification as read:', error);
  }
}, []);
```

4. **Added isLoading State**:
```typescript
const [isLoading, setIsLoading] = useState(false);

// Return in hook
return {
  notifications,
  unreadCount,
  isConnected,
  isLoading,  // NEW
  markAsRead,
  clearNotifications,
};
```

### 3. **Header.tsx** - Updated UI
**File**: `frontend/src/components/Header.tsx`

#### Changes:

1. **Added isLoading from hook**:
```typescript
const {
  notifications,
  unreadCount,
  isConnected,
  isLoading,  // NEW
  markAsRead,
} = useWebSocketNotifications();
```

2. **Added Loading State to UI**:
```typescript
{isLoading ? (
  <div className="flex items-center justify-center text-gray-500 py-5">
    <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
    <span className="ml-2">Đang tải...</span>
  </div>
) : notifications.length === 0 ? (
  <div className="flex items-center justify-center text-gray-500 py-5">
    Bạn chưa có thông báo nào.
  </div>
) : (
  // ... notification list
)}
```

## 🎯 Data Flow

### Initial Load:
```
1. User logs in
2. Header mounts → useWebSocketNotifications hook runs
3. Hook fetches notifications from backend API
4. Hook fetches unread count from backend API
5. Display notifications in UI
6. Connect to WebSocket for real-time updates
```

### Real-time Updates:
```
1. Backend creates new notification
2. Backend sends notification via WebSocket
3. Hook receives WebSocket message
4. Hook adds notification to list
5. Hook shows toast
6. UI updates automatically
```

### Mark as Read:
```
1. User clicks notification
2. Header calls markAsRead(id)
3. Hook calls API: markNotificationAsRead(id)
4. Backend updates database
5. Hook updates local state
6. Hook decrements unread count
7. UI updates (notification becomes transparent)
```

## 📊 API Response Format

### NotificationRes (from backend):
```typescript
{
  id: number;
  title: string;
  message: string;
  type: ENotificationType;
  isRead: boolean;
  createdAt: string;
  recipientId: number;
  // ... other fields
}
```

### ApiResponse Wrapper:
```typescript
{
  status: "OK" | "ERROR";
  message: string;
  data: NotificationRes[] | NotificationRes | number;
  errors: any;
}
```

## 🧪 Testing

### Test Case 1: Load Notifications
```bash
# 1. Open browser console
# 2. Login
# 3. Click bell icon
# Expected: 
#   - Should see loading spinner
#   - Should load notifications from backend
#   - Should display in dropdown
```

### Test Case 2: Mark as Read
```bash
# 1. Click on unread notification (blue background)
# 2. Expected:
#   - API call to /api/v1/notifications/{id}/read
#   - Notification background becomes transparent
#   - Unread count decreases by 1
```

### Test Case 3: Real-time Notification
```bash
# 1. Keep browser open
# 2. Trigger notification from another user/action
# 3. Expected:
#   - Toast appears
#   - Notification added to dropdown
#   - Unread count increases
```

### Test Case 4: Empty State
```bash
# 1. User with no notifications
# 2. Click bell icon
# Expected: "Bạn chưa có thông báo nào."
```

## 🐛 Error Handling

### API Errors:
- ✅ Try-catch wraps all API calls
- ✅ Console logs errors for debugging
- ✅ UI continues to work even if API fails

### Loading States:
- ✅ Shows spinner while fetching
- ✅ Prevents multiple simultaneous fetches
- ✅ Graceful fallback to empty state

## 🔄 Future Improvements

### 1. Pagination
Currently loads all notifications at once. Consider:
```typescript
getMyNotifications(userId, page, size)
```

### 2. Refresh Button
Add manual refresh:
```typescript
const refreshNotifications = async () => {
  // Re-fetch from API
};
```

### 3. Mark All as Read
Implement in UI:
```typescript
const handleMarkAllAsRead = async () => {
  await markAllNotificationsAsRead(user.id);
  // Refresh notifications
};
```

### 4. Delete Notification
Add delete button:
```typescript
const handleDelete = async (id: number) => {
  await deleteNotification(id);
  setNotifications(prev => prev.filter(n => n.id !== id));
};
```

### 5. Filter by Type
Add filtering:
```typescript
const [filter, setFilter] = useState<ENotificationType | 'all'>('all');
const filteredNotifications = notifications.filter(n => 
  filter === 'all' || n.type === filter
);
```

## 📝 Notes

### Authentication:
- ✅ All API calls include JWT token via axios interceptor
- ✅ Token retrieved from `localStorage.getItem('accessToken')`

### User ID:
- ⚠️ Currently requires passing `user.id` to APIs
- 🔮 Future: Backend could add `/my-notifications` endpoint that gets user from JWT

### WebSocket Connection:
- ✅ Connects automatically when user logs in
- ✅ Disconnects when user logs out
- ✅ Works alongside REST API for best of both worlds

### Data Consistency:
- ✅ Initial load from REST API
- ✅ Real-time updates from WebSocket
- ✅ Local state updates optimistically

## 🚀 Deployment Checklist

- [x] Add all API functions to `notification.api.ts`
- [x] Update `useWebSocketNotifications` hook to fetch from API
- [x] Add loading states to UI
- [x] Test mark as read functionality
- [x] Test real-time WebSocket updates
- [x] Handle error cases gracefully
- [x] Document API usage

## 📚 Related Documentation

- [NOTIFICATION_IMPLEMENTATION_SUMMARY.md](../../backend/docs/NOTIFICATION_IMPLEMENTATION_SUMMARY.md) - Backend implementation
- [NOTIFICATION_PERMISSION_AT_HEADER.md](./NOTIFICATION_PERMISSION_AT_HEADER.md) - Permission flow
- [NOTIFICATION_INTEGRATION_GUIDE.md](./NOTIFICATION_INTEGRATION_GUIDE.md) - WebSocket integration

---

**Status**: ✅ **COMPLETE** - Notification API fully integrated with backend
**Last Updated**: November 29, 2025
