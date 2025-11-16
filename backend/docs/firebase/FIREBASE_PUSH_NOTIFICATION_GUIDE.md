# Firebase Push Notification - Hướng Dẫn Cấu Hình

## 📱 Tổng Quan
Project này đã được tích hợp Firebase Cloud Messaging (FCM) để gửi push notifications đến mobile apps (Android/iOS) và web apps.

## 🔧 Các Bước Cấu Hình

### 1. Tạo Firebase Project

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** hoặc chọn project có sẵn
3. Nhập tên project và làm theo hướng dẫn

### 2. Lấy Service Account Key

1. Trong Firebase Console, vào **Project Settings** (⚙️ icon)
2. Chọn tab **"Service accounts"**
3. Click **"Generate new private key"**
4. Lưu file JSON được tải về (ví dụ: `firebase-service-account.json`)
5. **QUAN TRỌNG**: Đặt file này vào thư mục `backend/src/main/resources/firebase/`

### 3. Cấu Hình Environment Variables

Thêm vào file `.env` của bạn:

```bash
# Firebase Push Notification
FIREBASE_SERVICE_ACCOUNT_FILE=src/main/resources/firebase/firebase-service-account.json
```

**Lưu ý**: 
- Đường dẫn có thể là absolute path hoặc relative path từ thư mục backend
- Ví dụ absolute path: `/home/truong/IdeaProjects/project-tracking/backend/src/main/resources/firebase/firebase-service-account.json`

### 4. Thêm Firebase Service Account File vào .gitignore

```gitignore
# Firebase
**/firebase/firebase-service-account.json
**/firebase/*.json
```

## 📡 API Endpoints

### 1. Test Firebase Configuration
```http
GET /api/notifications/test
```

### 2. Gửi Notification Đến Device Token
```http
POST /api/notifications/send
Content-Type: application/json

{
  "token": "DEVICE_FCM_TOKEN_HERE",
  "title": "Test Notification",
  "body": "This is a test notification from Spring Boot!",
  "imageUrl": "https://example.com/image.png",
  "data": {
    "type": "PROJECT_UPDATE",
    "projectId": "123",
    "action": "COMMENT_ADDED"
  }
}
```

### 3. Gửi Notification Đến Topic
```http
POST /api/notifications/send-to-topic
Content-Type: application/json

{
  "topic": "all-users",
  "title": "System Announcement",
  "body": "New feature available!",
  "data": {
    "type": "ANNOUNCEMENT",
    "link": "/announcements/1"
  }
}
```

### 4. Subscribe Device Vào Topic
```http
POST /api/notifications/subscribe?token=DEVICE_TOKEN&topic=project-updates
```

### 5. Unsubscribe Device Khỏi Topic
```http
POST /api/notifications/unsubscribe?token=DEVICE_TOKEN&topic=project-updates
```

## 🔑 Lấy Device Token (FCM Token)

### Android App
```kotlin
// Thêm dependency vào build.gradle
implementation 'com.google.firebase:firebase-messaging:23.3.1'

// Lấy token
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        Log.d("FCM", "Token: $token")
        // Gửi token này lên backend để lưu vào database
    }
}
```

### iOS App
```swift
// AppDelegate.swift
import Firebase
import UserNotifications

func application(_ application: UIApplication, 
                 didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
    Messaging.messaging().apnsToken = deviceToken
    
    Messaging.messaging().token { token, error in
        if let token = token {
            print("FCM Token: \(token)")
            // Gửi token này lên backend
        }
    }
}
```

### Web App (JavaScript)
```javascript
// Import Firebase
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken } from 'firebase/messaging';

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

// Get registration token
getToken(messaging, { vapidKey: 'YOUR_VAPID_KEY' })
  .then((currentToken) => {
    if (currentToken) {
      console.log('FCM Token:', currentToken);
      // Gửi token lên backend
    }
  });
```

## 💾 Lưu Device Tokens

Bạn nên tạo một Entity để lưu device tokens của users:

```java
@Entity
@Table(name = "user_device_tokens")
public class UserDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private String fcmToken;
    
    @Column(nullable = false)
    private String deviceType; // ANDROID, IOS, WEB
    
    @Column
    private String deviceInfo;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime lastUsedAt;
}
```

## 🎯 Use Cases Thực Tế

### 1. Gửi Notification Khi Có Comment Mới
```java
@Service
public class ProjectCommentService {
    
    @Autowired
    private FirebaseMessagingService fcmService;
    
    @Autowired
    private UserDeviceTokenRepository tokenRepository;
    
    public void addComment(Long projectId, Comment comment) {
        // Save comment...
        
        // Gửi notification cho project owner
        Project project = projectRepository.findById(projectId).orElseThrow();
        User owner = project.getOwner();
        
        List<String> tokens = tokenRepository.findByUser(owner)
            .stream()
            .map(UserDeviceToken::getFcmToken)
            .toList();
        
        PushNotificationRequest notification = PushNotificationRequest.builder()
            .title("New Comment")
            .body(comment.getAuthor().getName() + " commented on your project")
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
    }
}
```

### 2. Gửi Notification Cho Tất Cả Users
```java
public void sendAnnouncementToAll(String title, String message) {
    PushNotificationRequest notification = PushNotificationRequest.builder()
        .topic("all-users")
        .title(title)
        .body(message)
        .build();
    
    fcmService.sendNotificationToTopic(notification);
}
```

## 🛠️ Testing

### Sử dụng Postman hoặc cURL

```bash
# Test configuration
curl http://localhost:9090/api/notifications/test

# Send notification
curl -X POST http://localhost:9090/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_DEVICE_TOKEN",
    "title": "Test",
    "body": "Hello from Spring Boot!"
  }'
```

### Sử dụng Swagger UI

Truy cập: `http://localhost:9090/swagger-ui.html`

## ⚠️ Lưu Ý Quan Trọng

1. **Bảo Mật**:
   - KHÔNG commit file `firebase-service-account.json` lên Git
   - Thêm file này vào `.gitignore`
   - Sử dụng environment variables cho production

2. **Token Management**:
   - FCM tokens có thể thay đổi, cần cập nhật định kỳ
   - Xử lý trường hợp token không hợp lệ hoặc expired
   - Xóa tokens của users đã logout

3. **Rate Limiting**:
   - Firebase có giới hạn về số lượng requests
   - Sử dụng batch sending cho nhiều devices

4. **Error Handling**:
   - Xử lý các lỗi như invalid token, device not registered
   - Log lỗi để debug

## 📚 Tài Liệu Tham Khảo

- [Firebase Cloud Messaging Documentation](https://firebase.google.com/docs/cloud-messaging)
- [Firebase Admin SDK for Java](https://firebase.google.com/docs/admin/setup)
- [FCM HTTP v1 API Reference](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages)

## 🔄 Next Steps

1. ✅ Thêm Firebase dependency vào pom.xml
2. ✅ Tạo Firebase Configuration
3. ✅ Tạo Service và Controller
4. ⏳ Tải Firebase Service Account JSON file
5. ⏳ Cấu hình environment variables
6. ⏳ Tạo Entity để lưu device tokens
7. ⏳ Tích hợp notifications vào business logic
8. ⏳ Test trên thiết bị thật

## 🆘 Troubleshooting

### Lỗi: "Failed to initialize Firebase Admin SDK"
- Kiểm tra đường dẫn đến service account file
- Đảm bảo file JSON hợp lệ
- Kiểm tra quyền đọc file

### Lỗi: "Invalid registration token"
- Token có thể đã expired hoặc không hợp lệ
- Yêu cầu client app lấy token mới

### Notification không hiển thị trên thiết bị
- Kiểm tra app có quyền nhận notifications
- Đảm bảo app đang chạy hoặc có background permission
- Kiểm tra cấu hình Firebase trên client app
