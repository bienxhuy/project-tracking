# 🚀 Firebase Push Notification - Quick Start (Frontend)

## ⚡ 5 Bước Nhanh

### 1. Cài đặt Dependencies
```bash
cd frontend
npm install firebase axios
```

### 2. Lấy Firebase Config

Truy cập [Firebase Console](https://console.firebase.google.com/):
- **Project Settings** > **General** > **Your apps** > **Web**
- Copy Firebase config
- **Cloud Messaging** > **Web Push certificates** > Copy VAPID key

### 3. Tạo file .env

```bash
cp .env.example .env
```

Điền thông tin:
```env
VITE_FIREBASE_API_KEY=AIzaSy...
VITE_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your-project-id
VITE_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=123456789012
VITE_FIREBASE_APP_ID=1:123456789012:web:xxxxx
VITE_FIREBASE_VAPID_KEY=BPxxxxxxxxxx
VITE_API_BASE_URL=http://localhost:9090
```

### 4. Cập nhật Service Worker

Sửa `public/firebase-messaging-sw.js` - thay config placeholder bằng Firebase config thật:

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

### 5. Thêm vào App

```tsx
// App.tsx
import { NotificationManager } from '@/components/NotificationManager';

function App() {
  return (
    <>
      {/* Your existing code */}
      <NotificationManager />
    </>
  );
}
```

### 6. Run!

```bash
npm run dev
```

Mở http://localhost:3000 → Click "Bật thông báo"

## 🧪 Test

### Lấy FCM Token
- Mở Browser Console (F12)
- Tìm log: `FCM Token: ....`
- Copy token

### Gửi Test Notification

#### Option 1: Via Swagger
1. Mở http://localhost:9090/swagger-ui.html
2. Login để lấy JWT token
3. Authorize với token
4. Gọi `/api/v1/device-tokens/test-notification`

#### Option 2: Via cURL
```bash
curl -X POST http://localhost:9090/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "token": "YOUR_FCM_TOKEN",
    "title": "Test",
    "body": "Hello!"
  }'
```

## ✅ Checklist

- [ ] `npm install firebase axios`
- [ ] Tạo `.env` với Firebase credentials
- [ ] Cập nhật `firebase-messaging-sw.js`
- [ ] Thêm `<NotificationManager />` vào App
- [ ] Run `npm run dev`
- [ ] Click "Bật thông báo"
- [ ] Copy FCM token từ console
- [ ] Test gửi notification từ backend

## 📚 Chi tiết đầy đủ

Xem [FIREBASE_PUSH_NOTIFICATION_GUIDE.md](./FIREBASE_PUSH_NOTIFICATION_GUIDE.md)

---

🎉 **Hoàn tất!** Frontend đã sẵn sàng nhận push notifications!
