# Testing Notification Permission Dialog

## Cách test Permission Dialog hiển thị

### 1. Reset toàn bộ permission states

**Mở Browser Console và chạy:**

```javascript
// Clear all notification-related data
localStorage.removeItem('notificationPermissionSkipped');
localStorage.removeItem('fcmToken');

// Log current state
console.log('Notification permission:', Notification.permission);
console.log('LocalStorage cleared');

// Reload page
location.reload();
```

### 2. Reset Browser Permission (nếu đã grant)

**Chrome/Edge:**
1. Click vào icon 🔒 (lock) bên trái URL bar
2. Click "Site settings"
3. Tìm "Notifications"
4. Chọn "Ask (default)" hoặc "Reset permissions"
5. Reload page

**Firefox:**
1. Click vào icon 🔒 (lock) bên trái URL bar
2. Click "Clear permissions and site data"
3. Reload page

### 3. Test Flow

#### Test Case 1: First time user
```bash
1. Clear localStorage (console command above)
2. Reset browser permission to "Ask"
3. Logout (if logged in)
4. Login với credentials
5. ✅ Dialog should appear after login
```

#### Test Case 2: User clicks "Cho phép"
```bash
1. Follow Test Case 1
2. Click "Cho phép" button
3. ✅ Browser permission prompt appears
4. Click "Allow" on browser prompt
5. ✅ FCM token registered
6. ✅ Navigate to home
7. Check console: "Device token registered successfully"
```

#### Test Case 3: User clicks "Để sau"
```bash
1. Follow Test Case 1
2. Click "Để sau" button
3. ✅ Navigate to home immediately
4. ✅ localStorage has 'notificationPermissionSkipped' = 'true'
5. Logout and login again
6. ✅ Dialog should NOT appear
```

#### Test Case 4: Permission already granted
```bash
1. Grant notification permission from previous login
2. Logout
3. Login again
4. ✅ Dialog should NOT appear
5. ✅ Automatically register token if not exists
6. ✅ Navigate to home
```

### 4. Debug Console Logs

Sau khi update, bạn sẽ thấy các logs này:

```javascript
// When authenticated
"User authenticated, checking notification permission..."
"Current permission: default" // hoặc "granted", "denied"
"Permission skipped: null" // hoặc "true"

// If showing dialog
"Showing notification permission dialog..."

// If already handled
"Permission already handled, navigating to home..."

// If permission granted but no token
"Permission granted but no token, requesting token..."

// When token registered
"Device token registered successfully"
```

### 5. Quick Reset Command

**Copy và paste vào console để reset nhanh:**

```javascript
// Complete reset
localStorage.clear();
console.log('✅ All localStorage cleared');
console.log('📊 Current permission:', Notification.permission);
console.log('🔄 Please reset browser notification permission manually');
console.log('   Chrome: Click 🔒 → Site settings → Notifications → Reset');
console.log('   Then logout and login again');
```

### 6. Force Show Dialog (Development only)

Nếu muốn test dialog UI mà không cần logout/login:

```javascript
// In browser console
const event = new CustomEvent('showNotificationDialog');
window.dispatchEvent(event);
```

Hoặc sửa tạm trong LoginPage.tsx:
```typescript
// Add this line to force show dialog
setShowNotificationDialog(true); // Test only
```

### 7. Verify Token Registration

**Check backend logs:**
```bash
# Look for this log in backend console
"✅ Sent Firebase push notification to 1 device(s) for user {userId}"
```

**Check database:**
```sql
SELECT * FROM user_device_tokens WHERE user_id = {your_user_id};
```

**Check via API:**
```bash
curl -X GET http://localhost:9090/api/v1/device-tokens/my-tokens \
  -H "Authorization: Bearer {your_token}"
```

---

## Common Issues

### Issue: Dialog không hiện

**Debug steps:**
1. Check console logs - có thấy "User authenticated..." không?
2. Check `Notification.permission` value
3. Check `localStorage.getItem('notificationPermissionSkipped')`
4. Xem có errors trong console không?

**Most common causes:**
- ❌ Browser đã cache permission = "granted"
- ❌ localStorage có 'notificationPermissionSkipped' = 'true'
- ❌ Browser không support Notification API

### Issue: Browser không hiện permission prompt

**Causes:**
- User đã deny permission trước đó
- Browser settings block notifications
- Testing trên HTTP (cần HTTPS hoặc localhost)

**Fix:**
- Reset site permissions
- Check browser settings → Notifications
- Use localhost hoặc HTTPS

### Issue: Token không register với backend

**Debug:**
```javascript
// Check if API is called
console.log('Calling registerDeviceToken...');
const response = await registerDeviceToken(token, 'WEB');
console.log('Response:', response);
```

**Common causes:**
- Backend không chạy
- CORS issues
- JWT token invalid
- API endpoint sai

---

## Expected Behavior

### ✅ Correct Flow:

```
User enters credentials
    ↓
Click "Đăng nhập"
    ↓
Login successful → isAuthenticated = true
    ↓
Check notification permission
    ↓
If not granted & not skipped:
    → Show dialog ✅
    → User clicks "Cho phép"
    → Browser prompt appears
    → User allows
    → FCM token obtained
    → Register with backend
    → Navigate to home
    
If granted or skipped:
    → Navigate to home immediately
```

---

## Testing Checklist

- [ ] Clear localStorage
- [ ] Reset browser notification permission
- [ ] Logout
- [ ] Login
- [ ] See permission dialog
- [ ] Click "Cho phép"
- [ ] See browser prompt
- [ ] Grant permission
- [ ] See console log: "Device token registered"
- [ ] Navigate to home
- [ ] Check localStorage has fcmToken
- [ ] Trigger notification from backend
- [ ] See toast notification
- [ ] See bell badge update

---

**Last Updated:** November 29, 2025
