# 🔥 Firebase Push Notification - Frontend Guide

## 📋 Setup Instructions

### 1. Firebase Console Setup

#### A. Create/Select Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project (same project used in backend)

#### B. Enable Cloud Messaging
1. Go to **Project Settings** (⚙️) > **Cloud Messaging**
2. Scroll to **Web configuration**
3. Click **Generate key pair** (if not already generated)
4. Copy the **VAPID key** (Web Push certificates)

#### C. Get Web App Config
1. Go to **Project Settings** > **General**
2. Scroll to **Your apps** section
3. Click **Web** icon (</>) to add a web app (if not already added)
4. Copy the Firebase configuration object:
```javascript
{
  apiKey: "...",
  authDomain: "...",
  projectId: "...",
  storageBucket: "...",
  messagingSenderId: "...",
  appId: "...",
  measurementId: "..."
}
```

### 2. Environment Configuration

Create `.env` file in frontend root directory:

```bash
cp .env.example .env
```

Then fill in your Firebase credentials:

```env
VITE_FIREBASE_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
VITE_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your-project-id
VITE_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=123456789012
VITE_FIREBASE_APP_ID=1:123456789012:web:xxxxxxxxxxxxx
VITE_FIREBASE_MEASUREMENT_ID=G-XXXXXXXXXX
VITE_FIREBASE_VAPID_KEY=BPXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxxx
VITE_API_BASE_URL=http://localhost:9090
```

### 3. Update Service Worker

Edit `public/firebase-messaging-sw.js` and replace the placeholder config with your actual Firebase config:

```javascript
firebase.initializeApp({
  apiKey: "YOUR_ACTUAL_API_KEY",
  authDomain: "YOUR_ACTUAL_AUTH_DOMAIN",
  projectId: "YOUR_ACTUAL_PROJECT_ID",
  storageBucket: "YOUR_ACTUAL_STORAGE_BUCKET",
  messagingSenderId: "YOUR_ACTUAL_SENDER_ID",
  appId: "YOUR_ACTUAL_APP_ID"
});
```

**⚠️ Important:** Service worker can't use environment variables, so you need to hardcode the config here.

### 4. Integrate into Your App

Update your `App.tsx` or main component:

```tsx
import { NotificationManager } from '@/components/NotificationManager';

function App() {
  return (
    <>
      {/* Your existing app content */}
      <NotificationManager />
    </>
  );
}
```

### 5. Run the Application

```bash
npm run dev
```

The app will run on `http://localhost:3000`

## 🎯 How It Works

### Flow Diagram

```
User Opens App
    ↓
NotificationManager renders
    ↓
Check browser support
    ↓
Request notification permission (if not granted)
    ↓
Get FCM token from Firebase
    ↓
Register token to backend API (/api/v1/device-tokens/register)
    ↓
Listen for messages (foreground & background)
    ↓
Display notifications
```

### Foreground vs Background

#### **Foreground (App is open)**
- Handled by `onMessageListener()` in `firebase.service.ts`
- Shows custom notification toast via React component
- Can handle data immediately in your app

#### **Background (App is closed/minimized)**
- Handled by `firebase-messaging-sw.js` service worker
- Shows browser native notification
- Clicking notification opens/focuses the app

## 🔧 Usage Examples

### Example 1: Auto-request Permission on Login

```tsx
// In your Login component
import { useNotification } from '@/hooks/useNotification';

function LoginPage() {
  const { requestPermission } = useNotification();

  const handleLogin = async (credentials) => {
    // Your login logic
    const user = await login(credentials);
    
    // Request notification permission after successful login
    if (user) {
      await requestPermission();
    }
  };

  return (
    // Your login form
  );
}
```

### Example 2: Manual Permission Request

```tsx
import { useNotification } from '@/hooks/useNotification';

function NotificationSettings() {
  const { permission, requestPermission, fcmToken } = useNotification();

  return (
    <div>
      <h2>Notification Settings</h2>
      
      <p>Status: {permission}</p>
      
      {permission !== 'granted' && (
        <button onClick={requestPermission}>
          Enable Notifications
        </button>
      )}
      
      {fcmToken && (
        <p className="text-xs">Token: {fcmToken.substring(0, 20)}...</p>
      )}
    </div>
  );
}
```

### Example 3: Handle Notification Click

Update `firebase-messaging-sw.js` to navigate to specific pages:

```javascript
self.addEventListener('notificationclick', (event) => {
  event.notification.close();

  // Get notification data
  const data = event.notification.data;
  
  // Determine URL based on notification type
  let urlToOpen = '/';
  
  if (data.type === 'NEW_COMMENT') {
    urlToOpen = `/projects/${data.projectId}`;
  } else if (data.type === 'PROJECT_UPDATE') {
    urlToOpen = `/projects/${data.projectId}/updates`;
  } else if (data.type === 'DEADLINE_WARNING') {
    urlToOpen = `/projects/${data.projectId}`;
  }
  
  // Open or focus the window
  event.waitUntil(
    clients.matchAll({ type: 'window' })
      .then((clientList) => {
        for (const client of clientList) {
          if (client.url.includes(urlToOpen) && 'focus' in client) {
            return client.focus();
          }
        }
        if (clients.openWindow) {
          return clients.openWindow(urlToOpen);
        }
      })
  );
});
```

### Example 4: Custom Notification Toast Component

```tsx
// components/NotificationToast.tsx
interface NotificationToastProps {
  title: string;
  body: string;
  type?: 'info' | 'success' | 'warning' | 'error';
  onClose: () => void;
}

export const NotificationToast = ({ title, body, type = 'info', onClose }: NotificationToastProps) => {
  const colors = {
    info: 'border-blue-500 bg-blue-50',
    success: 'border-green-500 bg-green-50',
    warning: 'border-yellow-500 bg-yellow-50',
    error: 'border-red-500 bg-red-50',
  };

  return (
    <div className={`fixed top-4 right-4 ${colors[type]} border-l-4 rounded-lg shadow-lg p-4 max-w-sm animate-slide-in`}>
      <div className="flex justify-between items-start">
        <div>
          <h4 className="font-semibold">{title}</h4>
          <p className="text-sm mt-1">{body}</p>
        </div>
        <button onClick={onClose} className="ml-4">
          <X size={18} />
        </button>
      </div>
    </div>
  );
};
```

## 🧪 Testing

### Test Notification from Backend

1. Get your FCM token from browser console or UI
2. Use Postman or cURL to send test notification:

```bash
curl -X POST http://localhost:9090/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "token": "YOUR_FCM_TOKEN_FROM_BROWSER",
    "title": "Test Notification",
    "body": "This is a test from backend!",
    "data": {
      "type": "TEST",
      "projectId": "123"
    }
  }'
```

### Test via Swagger UI

1. Open http://localhost:9090/swagger-ui.html
2. Navigate to **Push Notifications** section
3. Use `/api/v1/device-tokens/test-notification` endpoint

### Test via Firebase Console

1. Go to Firebase Console > Cloud Messaging
2. Click **Send your first message**
3. Enter title, body
4. Click **Send test message**
5. Enter your FCM token
6. Click **Test**

## 🐛 Troubleshooting

### Issue: "This browser does not support notifications"

**Solution:** Use a modern browser (Chrome, Firefox, Edge). Safari has limited support.

### Issue: Service Worker not found

**Solution:** 
- Make sure `firebase-messaging-sw.js` is in the `public/` folder
- Check browser DevTools > Application > Service Workers
- Try unregistering old service workers and refresh

### Issue: Permission denied automatically

**Solution:**
- User might have blocked notifications in browser settings
- Guide user to browser settings to re-enable

### Issue: No notification in foreground

**Solution:**
- Check if `onMessageListener()` is being called
- Check browser console for errors
- Verify notification component is rendered

### Issue: Token not sent to backend

**Solution:**
- Check if user is logged in (auth token required)
- Verify API endpoint is correct
- Check CORS settings on backend

## 🔒 Security Best Practices

1. **Never commit `.env` file**
   - Add `.env` to `.gitignore`
   - Use `.env.example` as template

2. **Validate tokens on backend**
   - Backend should verify FCM tokens before storing

3. **Implement token refresh**
   - FCM tokens can expire
   - Listen for token refresh events

4. **User consent**
   - Always ask user before requesting notification permission
   - Provide clear explanation of what notifications they'll receive

## 📱 Browser Support

| Browser | Support |
|---------|---------|
| Chrome  | ✅ Full |
| Firefox | ✅ Full |
| Edge    | ✅ Full |
| Safari  | ⚠️ Limited (macOS/iOS 16.4+) |
| Opera   | ✅ Full |

## 📚 Additional Resources

- [Firebase Cloud Messaging Web](https://firebase.google.com/docs/cloud-messaging/js/client)
- [Service Workers API](https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API)
- [Notifications API](https://developer.mozilla.org/en-US/docs/Web/API/Notifications_API)
- [Web Push Protocol](https://developers.google.com/web/fundamentals/push-notifications)

## ✅ Checklist

- [ ] Install Firebase SDK (`npm install firebase`)
- [ ] Create Firebase project in console
- [ ] Get Firebase config and VAPID key
- [ ] Create `.env` file with credentials
- [ ] Update `firebase-messaging-sw.js` with config
- [ ] Add `<NotificationManager />` to App
- [ ] Request permission on user login
- [ ] Test foreground notifications
- [ ] Test background notifications
- [ ] Handle notification clicks
- [ ] Test with backend API

---

## 🎉 Done!

Your React app now supports Firebase Push Notifications!
