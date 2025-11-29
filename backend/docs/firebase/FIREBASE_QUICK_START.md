# 🚀 Firebase Push Notification - Quick Start

## Các Bước Nhanh Để Bắt Đầu

### 1️⃣ Lấy Firebase Service Account File

1. Truy cập https://console.firebase.google.com/
2. Chọn project hoặc tạo mới
3. **Project Settings** (⚙️) → **Service Accounts** → **Generate new private key**
4. Lưu file JSON vào: `backend/src/main/resources/firebase/firebase-service-account.json`

### 2️⃣ Cấu Hình Environment Variable

Thêm vào file `.env`:
```bash
FIREBASE_SERVICE_ACCOUNT_FILE=src/main/resources/firebase/firebase-service-account.json
```

### 3️⃣ Chạy Application

```bash
cd backend
./mvnw spring-boot:run
```

### 4️⃣ Test API

Mở Swagger UI: http://localhost:9090/swagger-ui.html

Hoặc dùng cURL:
```bash
# Test configuration
curl http://localhost:9090/api/notifications/test

# Send notification (cần device token từ mobile/web app)
curl -X POST http://localhost:9090/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_DEVICE_TOKEN_HERE",
    "title": "Test Notification",
    "body": "Hello from Spring Boot!"
  }'
```

## 📱 Lấy Device Token

### Android
```kotlin
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    val token = task.result
    // Gửi token lên backend qua API /api/device-tokens/register
}
```

### iOS
```swift
Messaging.messaging().token { token, error in
    // Gửi token lên backend
}
```

### Web
```javascript
import { getMessaging, getToken } from 'firebase/messaging';

const messaging = getMessaging();
getToken(messaging, { vapidKey: 'YOUR_VAPID_KEY' })
  .then((token) => {
    // Gửi token lên backend
  });
```

## 🎯 Use Case: Gửi Notification Khi Có Comment

```java
@Service
public class CommentService {
    @Autowired
    private FirebaseMessagingService fcmService;
    
    @Autowired
    private UserDeviceTokenService tokenService;
    
    public void notifyProjectOwner(Project project, Comment comment) {
        List<String> tokens = tokenService.getActiveFcmTokens(project.getOwner());
        
        PushNotificationRequest notification = PushNotificationRequest.builder()
            .title("New Comment")
            .body(comment.getAuthor().getName() + " commented on your project")
            .data(Map.of(
                "projectId", project.getId().toString(),
                "commentId", comment.getId().toString()
            ))
            .build();
        
        tokens.forEach(token -> {
            notification.setToken(token);
            fcmService.sendNotificationToToken(notification);
        });
    }
}
```

## 📚 Xem Thêm

- Chi tiết đầy đủ: [FIREBASE_PUSH_NOTIFICATION_GUIDE.md](FIREBASE_PUSH_NOTIFICATION_GUIDE.md)
- Firebase Docs: https://firebase.google.com/docs/cloud-messaging

## ✅ Checklist

- [ ] Tải Firebase Service Account JSON file
- [ ] Đặt file vào `backend/src/main/resources/firebase/`
- [ ] Thêm `FIREBASE_SERVICE_ACCOUNT_FILE` vào `.env`
- [ ] Chạy application
- [ ] Test API `/api/notifications/test`
- [ ] Lấy device token từ client app
- [ ] Gửi notification thử nghiệm
- [ ] Tích hợp vào business logic
