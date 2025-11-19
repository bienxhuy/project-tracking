# 📱 Firebase Push Notification - Complete Implementation

## 🎯 Tổng Quan

Project đã được tích hợp **Firebase Cloud Messaging (FCM)** để gửi push notifications cho cả **Backend (Spring Boot)** và **Frontend (React + TypeScript)**.

---

## 📦 **Files Đã Tạo**

### **Frontend**

```
frontend/
├── src/
│   ├── config/
│   │   └── firebase.config.ts           # Firebase configuration
│   ├── services/
│   │   ├── firebase.service.ts          # Firebase messaging service
│   │   └── notification.api.ts          # API calls to backend
│   ├── hooks/
│   │   └── useNotification.ts           # React hook for notifications
│   ├── components/
│   │   └── NotificationManager.tsx      # Notification UI component
│   └── index.css                        # Added animations
├── public/
│   └── firebase-messaging-sw.js         # Service worker for background notifications
├── .env.example                         # Environment variables template
├── FIREBASE_PUSH_NOTIFICATION_GUIDE.md  # Detailed guide
└── FIREBASE_QUICK_START.md             # Quick start guide
```

### **Backend**

```
backend/
├── src/main/java/POSE_Project_Tracking/Blog/
│   ├── config/
│   │   └── FirebaseConfig.java          # Firebase Admin SDK config
│   ├── controller/
│   │   ├── PushNotificationController.java    # Notification APIs
│   │   └── DeviceTokenController.java         # Device token management
│   ├── service/
│   │   ├── FirebaseMessagingService.java      # FCM service
│   │   └── UserDeviceTokenService.java        # Token management
│   ├── dto/
│   │   ├── PushNotificationRequest.java
│   │   └── PushNotificationResponse.java
│   ├── entity/
│   │   └── UserDeviceToken.java         # Entity for storing tokens
│   └── repository/
│       └── UserDeviceTokenRepository.java
├── src/main/resources/
│   ├── firebase/
│   │   ├── .gitignore
│   │   └── README.md
│   └── application.properties           # Added Firebase config
├── pom.xml                              # Added firebase-admin dependency
├── .env.firebase.example
├── FIREBASE_PUSH_NOTIFICATION_GUIDE.md
├── FIREBASE_QUICK_START.md
└── FIREBASE_IMPLEMENTATION_CHECKLIST.md
```

---

## 🚀 **Quick Start**

### **Backend Setup**

1. **Lấy Firebase Service Account:**
   - Truy cập https://console.firebase.google.com/
   - Project Settings > Service Accounts
   - Generate new private key
   - Lưu file JSON vào: `backend/src/main/resources/firebase/firebase-service-account.json`

2. **Cấu hình .env:**
   ```bash
   FIREBASE_SERVICE_ACCOUNT_FILE=src/main/resources/firebase/firebase-service-account.json
   ```

3. **Run backend:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

### **Frontend Setup**

1. **Install dependencies:**
   ```bash
   cd frontend
   npm install firebase axios
   ```

2. **Lấy Firebase Web Config:**
   - Firebase Console > Project Settings > General > Your apps > Web
   - Copy config và VAPID key

3. **Tạo .env:**
   ```bash
   cp .env.example .env
   # Fill in Firebase credentials
   ```

4. **Cập nhật Service Worker:**
   - Edit `public/firebase-messaging-sw.js`
   - Replace placeholder config with your Firebase config

5. **Add to App:**
   ```tsx
   import { NotificationManager } from '@/components/NotificationManager';
   
   function App() {
     return (
       <>
         {/* Your app */}
         <NotificationManager />
       </>
     );
   }
   ```

6. **Run frontend:**
   ```bash
   npm run dev
   ```

---

## 🎯 **APIs Available**

### **Notification APIs**
- `POST /api/v1/notifications/send` - Send to specific device
- `POST /api/v1/notifications/send-to-topic` - Send to topic
- `POST /api/v1/notifications/subscribe` - Subscribe to topic
- `POST /api/v1/notifications/unsubscribe` - Unsubscribe from topic

### **Device Token APIs**
- `POST /api/v1/device-tokens/register` - Register device token
- `GET /api/v1/device-tokens/my-tokens` - Get user's tokens
- `DELETE /api/v1/device-tokens/{token}` - Delete token
- `POST /api/v1/device-tokens/test-notification` - Send test notification

---

## 🧪 **Testing**

### **1. Test Backend API**

```bash
# Test configuration
curl http://localhost:9090/api/v1/notifications/test

# Send notification (need FCM token from frontend)
curl -X POST http://localhost:9090/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "token": "FCM_TOKEN_FROM_BROWSER",
    "title": "Test",
    "body": "Hello!"
  }'
```

### **2. Test Frontend**

1. Open http://localhost:3000
2. Click "Bật thông báo" button
3. Grant permission
4. Check browser console for FCM token
5. Use token to test from backend

### **3. Via Swagger UI**

http://localhost:9090/swagger-ui.html

---

## 📊 **Flow Diagram**

```
┌─────────────────────────────────────────────────────────────┐
│                    USER OPENS FRONTEND                       │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         NotificationManager Component Renders                │
│  - Check browser support                                     │
│  - Request notification permission                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              Get FCM Token from Firebase                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│    POST /api/v1/device-tokens/register (Backend)            │
│    - Save token to database                                  │
│    - Associate with logged-in user                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              READY TO RECEIVE NOTIFICATIONS                  │
└─────────────────────────────────────────────────────────────┘

When notification is sent:
Backend → Firebase → Frontend (if app open) / Service Worker (if app closed)
```

---

## 🔐 **Security**

✅ **Backend:**
- Service account file is gitignored
- Environment variables for sensitive data
- JWT authentication for APIs

✅ **Frontend:**
- .env file is gitignored
- HTTPS required in production
- User consent before requesting permission

---

## 📚 **Documentation**

### **Detailed Guides:**
- **Frontend:** `frontend/FIREBASE_PUSH_NOTIFICATION_GUIDE.md`
- **Backend:** `backend/FIREBASE_PUSH_NOTIFICATION_GUIDE.md`

### **Quick Starts:**
- **Frontend:** `frontend/FIREBASE_QUICK_START.md`
- **Backend:** `backend/FIREBASE_QUICK_START.md`

### **Checklist:**
- **Backend:** `backend/FIREBASE_IMPLEMENTATION_CHECKLIST.md`

---

## ✅ **Implementation Checklist**

### **Backend**
- [x] Add firebase-admin dependency
- [x] Create FirebaseConfig
- [x] Create services & controllers
- [x] Create Entity & Repository
- [ ] Get Firebase service account JSON
- [ ] Configure .env
- [ ] Test APIs

### **Frontend**
- [x] Install firebase SDK
- [x] Create firebase service
- [x] Create notification hook
- [x] Create UI component
- [x] Create service worker
- [ ] Get Firebase web config
- [ ] Configure .env
- [ ] Update service worker config
- [ ] Add to App component
- [ ] Test in browser

---

## 🎉 **Next Steps**

1. **Complete Firebase Setup:**
   - Get service account JSON (backend)
   - Get web config & VAPID key (frontend)

2. **Configure Environment:**
   - Update `.env` files with credentials
   - Update service worker with config

3. **Test Integration:**
   - Run both backend & frontend
   - Request permission
   - Send test notifications

4. **Integrate with Business Logic:**
   - Send notifications on new comments
   - Send notifications on deadlines
   - Send notifications on project updates

---

## 🆘 **Support**

Nếu gặp vấn đề, check:
1. Browser console (F12) for errors
2. Backend logs for API errors
3. Firebase Console > Cloud Messaging for debugging
4. Documentation files for detailed guides

---

## 🎯 **Production Checklist**

- [ ] Use HTTPS for frontend
- [ ] Secure environment variables
- [ ] Test on multiple browsers
- [ ] Test foreground & background notifications
- [ ] Implement error handling
- [ ] Add analytics/monitoring
- [ ] Document user flow
- [ ] Test token refresh mechanism

---

**🚀 Firebase Push Notification implementation complete!**
