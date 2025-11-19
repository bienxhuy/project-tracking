# 📋 TỔNG HỢP CÁC BƯỚC TRIỂN KHAI FIREBASE PUSH NOTIFICATION

## ✅ Đã Hoàn Thành

### 1. Backend Code (Spring Boot)

#### ✔️ Dependencies
- Đã thêm `firebase-admin` SDK vào `pom.xml`

#### ✔️ Configuration
- **FirebaseConfig.java** - Khởi tạo Firebase Admin SDK
- **application.properties** - Thêm Firebase configuration

#### ✔️ DTOs
- **PushNotificationRequest.java** - Request model cho notifications
- **PushNotificationResponse.java** - Response model

#### ✔️ Entities
- **UserDeviceToken.java** - Entity lưu FCM tokens của users

#### ✔️ Repositories
- **UserDeviceTokenRepository.java** - Repository cho device tokens

#### ✔️ Services
- **FirebaseMessagingService.java** - Service gửi notifications
- **UserDeviceTokenService.java** - Service quản lý device tokens

#### ✔️ Controllers
- **PushNotificationController.java** - API gửi notifications
- **DeviceTokenController.java** - API quản lý device tokens

#### ✔️ Documentation
- **FIREBASE_PUSH_NOTIFICATION_GUIDE.md** - Hướng dẫn chi tiết đầy đủ
- **FIREBASE_QUICK_START.md** - Hướng dẫn nhanh
- **firebase/README.md** - Hướng dẫn setup credentials

#### ✔️ Environment Files
- **.env.firebase.example** - Mẫu cấu hình Firebase

## ⏳ Các Bước Tiếp Theo (Cần Làm)

### 2. Firebase Console Setup

1. **Tạo/Chọn Firebase Project**
   - Truy cập: https://console.firebase.google.com/
   - Tạo project mới hoặc chọn project có sẵn

2. **Lấy Service Account Credentials**
   - Vào Project Settings (⚙️) > Service Accounts
   - Click "Generate new private key"
   - Lưu file JSON tải về

3. **Đặt Credentials File**
   - Di chuyển file JSON vào: `backend/src/main/resources/firebase/firebase-service-account.json`
   - File này đã được gitignore tự động

### 3. Environment Configuration

Thêm vào file `.env` của bạn:
```bash
FIREBASE_SERVICE_ACCOUNT_FILE=src/main/resources/firebase/firebase-service-account.json
```

### 4. Build & Run

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### 5. Test APIs

#### Test Configuration
```bash
curl http://localhost:9090/api/notifications/test
```

#### Test Send Notification (cần device token)
```bash
curl -X POST http://localhost:9090/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_DEVICE_TOKEN",
    "title": "Test",
    "body": "Hello!"
  }'
```

### 6. Client App Integration

#### Android Setup
```kotlin
// build.gradle
implementation 'com.google.firebase:firebase-messaging:23.3.1'

// Get token
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // POST to /api/device-tokens/register
    }
}
```

#### iOS Setup
```swift
import Firebase
import FirebaseMessaging

Messaging.messaging().token { token, error in
    if let token = token {
        // POST to /api/device-tokens/register
    }
}
```

#### Web Setup
```javascript
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken } from 'firebase/messaging';

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

getToken(messaging, { vapidKey: 'YOUR_VAPID_KEY' })
  .then((token) => {
    // POST to /api/device-tokens/register
  });
```

## 🎯 Available APIs

### Notification APIs
```
GET    /api/notifications/test                 - Test Firebase config
POST   /api/notifications/send                 - Send to device token
POST   /api/notifications/send-to-topic        - Send to topic
POST   /api/notifications/subscribe            - Subscribe to topic
POST   /api/notifications/unsubscribe          - Unsubscribe from topic
```

### Device Token APIs
```
POST   /api/device-tokens/register             - Register device token
GET    /api/device-tokens/my-tokens            - Get user's tokens
DELETE /api/device-tokens/{token}              - Delete token
POST   /api/device-tokens/deactivate/{token}   - Deactivate token
DELETE /api/device-tokens/my-tokens/all        - Delete all user tokens
POST   /api/device-tokens/test-notification    - Send test notification
```

## 📊 Database Schema

Bảng `user_device_tokens` sẽ được tạo tự động với các cột:
- `id` - Primary key
- `user_id` - Foreign key to users table
- `fcm_token` - Firebase Cloud Messaging token (unique)
- `device_type` - ANDROID, IOS, WEB
- `device_info` - Optional device information
- `is_active` - Token status
- `created_at` - Creation timestamp
- `last_used_at` - Last usage timestamp

## 🔐 Security Checklist

- ✅ Service account file đã được gitignore
- ✅ Sử dụng environment variables cho sensitive data
- ⏳ Implement authentication cho APIs (nếu chưa có)
- ⏳ Validate device tokens trước khi lưu
- ⏳ Implement rate limiting cho notification APIs

## 📝 Example Integration

### Ví dụ: Gửi notification khi có comment mới

```java
@Service
public class ProjectCommentService {
    
    @Autowired
    private FirebaseMessagingService fcmService;
    
    @Autowired
    private UserDeviceTokenService tokenService;
    
    public Comment addComment(Long projectId, CommentDTO dto) {
        // Save comment
        Comment comment = saveComment(projectId, dto);
        
        // Get project owner
        Project project = projectRepository.findById(projectId).orElseThrow();
        User owner = project.getOwner();
        
        // Send notification to owner's devices
        List<String> tokens = tokenService.getActiveFcmTokens(owner);
        
        PushNotificationRequest notification = PushNotificationRequest.builder()
            .title("New Comment")
            .body(comment.getAuthor().getName() + " commented: " + comment.getContent())
            .data(Map.of(
                "type", "NEW_COMMENT",
                "projectId", projectId.toString(),
                "commentId", comment.getId().toString()
            ))
            .build();
        
        tokens.forEach(token -> {
            notification.setToken(token);
            fcmService.sendNotificationToToken(notification);
        });
        
        return comment;
    }
}
```

## 🐛 Troubleshooting

### Lỗi: "Failed to initialize Firebase"
- Kiểm tra đường dẫn file service account
- Đảm bảo file JSON hợp lệ
- Kiểm tra permissions đọc file

### Lỗi: "Invalid registration token"
- Token có thể đã expired
- Yêu cầu client refresh token

### Notification không hiển thị
- Kiểm tra app có permission nhận notifications
- Kiểm tra Firebase config trên client app
- Kiểm tra device có kết nối internet

## 📚 Resources

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Cloud Messaging Docs](https://firebase.google.com/docs/cloud-messaging)
- [Firebase Admin SDK Java](https://firebase.google.com/docs/admin/setup)
- Swagger UI: http://localhost:9090/swagger-ui.html

## 🎉 Done!

Bạn đã hoàn thành việc tích hợp Firebase Push Notification vào Spring Boot project!

Các bước quan trọng còn lại:
1. Lấy Firebase Service Account file
2. Cấu hình .env
3. Chạy và test application
4. Tích hợp vào client apps (Android/iOS/Web)
5. Implement notification logic vào business flows
