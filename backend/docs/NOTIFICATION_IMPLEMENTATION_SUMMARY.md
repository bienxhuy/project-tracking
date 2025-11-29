# 📋 TỔNG HỢP TRIỂN KHAI HỆ THỐNG THÔNG BÁO (NOTIFICATION SYSTEM)

## 📅 Ngày cập nhật: 29/11/2025

---

## 🎯 MỤC TIÊU

Triển khai hệ thống thông báo real-time cho các hoạt động quan trọng trong dự án, bao gồm:
- ✅ Thông báo khi sinh viên được thêm vào dự án
- ✅ Thông báo khi sinh viên được giao nhiệm vụ
- ✅ Thông báo khi báo cáo được submit
- ✅ Thông báo khi có comment mới
- ✅ Thông báo khi task được đánh dấu hoàn thành
- ✅ Thông báo deadline sắp hết hạn (1 ngày)
- ✅ Thông báo khi task/milestone/report/project bị khóa
- ✅ Thông báo khi được mention trong comment
- ✅ Thông báo khi objective & content được define

---

## 📦 CÁC THÀNH PHẦN ĐÃ TRIỂN KHAI

### 1. **Enums - Notification Types**

**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/enums/ENotificationType.java`

```java
public enum ENotificationType {
    // Project notifications
    PROJECT_ASSIGNED,              // Sinh viên được thêm vào dự án
    PROJECT_CONTENT_DEFINED,       // Objective & content được define
    PROJECT_LOCKED,                // Project bị khóa
    PROJECT_CONTENT_LOCKED,        // Project content bị khóa
    
    // Task notifications
    TASK_ASSIGNED,                 // Task được giao cho sinh viên
    TASK_COMPLETED,                // Task được đánh dấu hoàn thành
    TASK_LOCKED,                   // Task bị khóa
    
    // Milestone notifications
    MILESTONE_LOCKED,              // Milestone bị khóa
    MILESTONE_DEADLINE_APPROACHING, // Milestone sắp hết hạn (1 ngày)
    
    // Report notifications
    REPORT_SUBMITTED,              // Report được submit
    REPORT_LOCKED,                 // Report bị khóa
    
    // Comment notifications
    COMMENT_ADDED,                 // Comment được thêm vào task
    MENTION,                       // User được mention trong comment
    
    // Deadline notifications
    PROJECT_DEADLINE_APPROACHING,  // Project sắp hết hạn (1 ngày)
    TASK_DEADLINE_APPROACHING      // Task sắp hết hạn (1 ngày)
}
```

---

### 2. **Helper Service - NotificationHelperService**

**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/NotificationHelperService.java`

**Chức năng**:
- Tạo notification cho một user
- Tạo notification cho nhiều users
- Tạo notification cho tất cả thành viên trong project (bao gồm instructor)
- Tạo notification cho chỉ sinh viên (không gửi cho instructor)
- Parse @mentions từ comment text
- Tự động gửi WebSocket notification realtime

**Methods**:
```java
// Tạo notification cho 1 user
createNotification(User, title, message, type, referenceId, referenceType, triggeredBy)

// Tạo notification cho nhiều users
createNotificationsForUsers(List<User>, title, message, type, referenceId, referenceType, triggeredBy)

// Tạo notification cho toàn bộ thành viên project (sinh viên + giảng viên)
createNotificationsForAllProjectMembers(Project, title, message, type, referenceId, referenceType, triggeredBy)

// Tạo notification cho chỉ sinh viên
createNotificationsForStudentsOnly(Project, title, message, type, referenceId, referenceType, triggeredBy)

// Extract mentioned user IDs từ comment
extractMentionedUserIds(String text)
```

---

### 3. **Scheduler - Deadline Notifications**

**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/scheduler/DeadlineNotificationScheduler.java`

**Chức năng**:
- Tự động kiểm tra deadline mỗi ngày lúc 8:00 sáng
- Gửi thông báo cho các project/milestone/task sắp hết hạn (còn 1 ngày)
- Chỉ thông báo cho items chưa hoàn thành

**Schedule**: `@Scheduled(cron = "0 0 8 * * *")` - Chạy lúc 8:00 AM mỗi ngày

**Methods**:
- `checkProjectDeadlines()` - Kiểm tra project deadlines
- `checkMilestoneDeadlines()` - Kiểm tra milestone deadlines
- `checkTaskDeadlines()` - Kiểm tra task deadlines

---

### 4. **Repository Extensions**

#### ProjectRepository
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/repository/ProjectRepository.java`

```java
List<Project> findByEndDate(LocalDate endDate);
```

#### MilestoneRepository
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/repository/MilestoneRepository.java`

```java
List<Milestone> findByEndDate(LocalDate endDate);
```

#### TaskRepository
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/repository/TaskRepository.java`

```java
List<Task> findByEndDate(LocalDate endDate);
```

---

### 5. **Service Implementation Updates**

#### ✅ ProjectMemberServiceImpl
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/impl/ProjectMemberServiceImpl.java`

**Notifications**:
- ✅ `PROJECT_ASSIGNED` - Khi thêm member vào project

```java
// Trong method addMember()
notificationHelperService.createNotification(
    user, 
    "Bạn được thêm vào dự án",
    String.format("Bạn đã được thêm vào dự án \"%s\"", project.getTitle()),
    ENotificationType.PROJECT_ASSIGNED,
    project.getId(),
    "PROJECT",
    triggeredBy
);
```

---

#### ✅ TaskServiceImpl
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/impl/TaskServiceImpl.java`

**Notifications**:
1. ✅ `TASK_ASSIGNED` - Khi assign task cho user
2. ✅ `TASK_COMPLETED` - Khi task được đánh dấu hoàn thành
3. ✅ `TASK_LOCKED` - Khi task bị khóa

```java
// 1. Trong method assignTask()
notificationHelperService.createNotification(
    user,
    "Bạn được giao nhiệm vụ mới",
    String.format("Bạn được giao nhiệm vụ \"%s\" trong dự án \"%s\"", 
        task.getTitle(), task.getProject().getTitle()),
    ENotificationType.TASK_ASSIGNED,
    task.getId(),
    "TASK",
    triggeredBy
);

// 2. Trong method markTaskAsCompleted()
notificationHelperService.createNotificationsForAllProjectMembers(
    task.getProject(),
    "Nhiệm vụ hoàn thành",
    String.format("Nhiệm vụ \"%s\" đã được đánh dấu hoàn thành", task.getTitle()),
    ENotificationType.TASK_COMPLETED,
    task.getId(),
    "TASK",
    triggeredBy
);

// 3. Trong method lockTaskWithChildren()
notificationHelperService.createNotificationsForStudentsOnly(
    task.getProject(),
    "Nhiệm vụ bị khóa",
    String.format("Nhiệm vụ \"%s\" đã bị khóa bởi giảng viên", task.getTitle()),
    ENotificationType.TASK_LOCKED,
    task.getId(),
    "TASK",
    currentUser
);
```

---

#### ✅ ReportServiceImpl
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/impl/ReportServiceImpl.java`

**Notifications**:
1. ✅ `REPORT_SUBMITTED` - Khi report được submit
2. ✅ `REPORT_LOCKED` - Khi report bị khóa

```java
// 1. Trong method createReport()
notificationHelperService.createNotificationsForAllProjectMembers(
    project,
    "Báo cáo mới được submit",
    String.format("%s đã submit báo cáo \"%s\"", 
        author.getName(), report.getTitle()),
    ENotificationType.REPORT_SUBMITTED,
    report.getId(),
    "REPORT",
    author
);

// 2. Trong method lockReportWithChildren()
notificationHelperService.createNotificationsForStudentsOnly(
    report.getProject(),
    "Báo cáo bị khóa",
    String.format("Báo cáo \"%s\" đã bị khóa bởi giảng viên", report.getTitle()),
    ENotificationType.REPORT_LOCKED,
    report.getId(),
    "REPORT",
    currentUser
);
```

---

#### ✅ CommentServiceImpl
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/impl/CommentServiceImpl.java`

**Notifications**:
1. ✅ `COMMENT_ADDED` - Khi comment được thêm
2. ✅ `MENTION` - Khi user được mention trong comment

```java
// 1. Notification cho toàn bộ thành viên
notificationHelperService.createNotificationsForAllProjectMembers(
    project,
    "Bình luận mới",
    String.format("%s đã bình luận trong %s", 
        author.getName(), context),
    ENotificationType.COMMENT_ADDED,
    comment.getId(),
    "COMMENT",
    author
);

// 2. Notification riêng cho user được mention
List<Long> mentionedUserIds = notificationHelperService.extractMentionedUserIds(comment.getContent());
if (!mentionedUserIds.isEmpty()) {
    List<User> mentionedUsers = userRepository.findAllById(mentionedUserIds);
    
    notificationHelperService.createNotificationsForUsers(
        mentionedUsers,
        "Bạn được nhắc đến",
        String.format("%s đã nhắc đến bạn trong một bình luận", author.getName()),
        ENotificationType.MENTION,
        comment.getId(),
        "COMMENT",
        author
    );
}
```

**Format mention**: `@[userId]` ví dụ: `@[123]`

---

#### ✅ ProjectServiceImpl
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/impl/ProjectServiceImpl.java`

**Notifications**:
1. ✅ `PROJECT_LOCKED` - Khi project bị khóa
2. ✅ `PROJECT_CONTENT_LOCKED` - Khi project content bị khóa
3. ✅ `PROJECT_CONTENT_DEFINED` - Khi objective & content được update

```java
// 1. Trong method lockProject()
notificationHelperService.createNotificationsForStudentsOnly(
    project,
    "Dự án bị khóa",
    String.format("Dự án \"%s\" đã bị khóa bởi giảng viên", project.getTitle()),
    ENotificationType.PROJECT_LOCKED,
    project.getId(),
    "PROJECT",
    currentUser
);

// 2. Trong method lockProjectContent()
notificationHelperService.createNotificationsForStudentsOnly(
    project,
    "Nội dung dự án bị khóa",
    String.format("Nội dung dự án \"%s\" đã bị khóa bởi giảng viên", project.getTitle()),
    ENotificationType.PROJECT_CONTENT_LOCKED,
    project.getId(),
    "PROJECT",
    currentUser
);

// 3. Trong method updateProjectContent()
notificationHelperService.createNotificationsForAllProjectMembers(
    project,
    "Nội dung dự án được cập nhật",
    String.format("%s đã định nghĩa nội dung cho dự án \"%s\"", 
        currentUser.getName(), project.getTitle()),
    ENotificationType.PROJECT_CONTENT_DEFINED,
    project.getId(),
    "PROJECT",
    currentUser
);
```

---

#### ✅ MilestoneServiceImpl
**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/service/impl/MilestoneServiceImpl.java`

**Notifications**:
- ✅ `MILESTONE_LOCKED` - Khi milestone bị khóa

```java
// Trong method lockMilestoneWithChildren()
notificationHelperService.createNotificationsForStudentsOnly(
    milestone.getProject(),
    "Milestone bị khóa",
    String.format("Milestone \"%s\" đã bị khóa bởi giảng viên", milestone.getTitle()),
    ENotificationType.MILESTONE_LOCKED,
    milestone.getId(),
    "MILESTONE",
    currentUser
);
```

---

### 6. **Application Configuration**

**File**: `backend/src/main/java/POSE_Project_Tracking/Blog/ProjectTrackingApplication.java`

```java
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling  // ✅ Enable scheduling cho deadline notifications
@EntityScan("POSE_Project_Tracking.Blog.entity")
public class ProjectTrackingApplication {
    // ...
}
```

---

## 📊 MA TRẬN NOTIFICATION

| Sự kiện | Notification Type | Người nhận | Trigger Point |
|---------|------------------|-----------|---------------|
| Sinh viên được thêm vào project | `PROJECT_ASSIGNED` | Sinh viên được thêm | `ProjectMemberServiceImpl.addMember()` |
| Task được giao | `TASK_ASSIGNED` | Sinh viên được giao | `TaskServiceImpl.assignTask()` |
| Report được submit | `REPORT_SUBMITTED` | Toàn bộ thành viên (SV + GV) | `ReportServiceImpl.createReport()` |
| Comment được thêm | `COMMENT_ADDED` | Toàn bộ thành viên (SV + GV) | `CommentServiceImpl.createComment()` |
| User được mention | `MENTION` | User được mention | `CommentServiceImpl.createComment()` |
| Task hoàn thành | `TASK_COMPLETED` | Toàn bộ thành viên (SV + GV) | `TaskServiceImpl.markTaskAsCompleted()` |
| Task bị khóa | `TASK_LOCKED` | Chỉ sinh viên | `TaskServiceImpl.lockTaskWithChildren()` |
| Milestone bị khóa | `MILESTONE_LOCKED` | Chỉ sinh viên | `MilestoneServiceImpl.lockMilestoneWithChildren()` |
| Report bị khóa | `REPORT_LOCKED` | Chỉ sinh viên | `ReportServiceImpl.lockReportWithChildren()` |
| Project bị khóa | `PROJECT_LOCKED` | Chỉ sinh viên | `ProjectServiceImpl.lockProject()` |
| Project content bị khóa | `PROJECT_CONTENT_LOCKED` | Chỉ sinh viên | `ProjectServiceImpl.lockProjectContent()` |
| Project content được define | `PROJECT_CONTENT_DEFINED` | Toàn bộ thành viên (SV + GV) | `ProjectServiceImpl.updateProjectContent()` |
| Project deadline (1 ngày) | `PROJECT_DEADLINE_APPROACHING` | Toàn bộ thành viên (SV + GV) | Scheduler (8:00 AM) |
| Milestone deadline (1 ngày) | `MILESTONE_DEADLINE_APPROACHING` | Toàn bộ thành viên (SV + GV) | Scheduler (8:00 AM) |
| Task deadline (1 ngày) | `TASK_DEADLINE_APPROACHING` | Toàn bộ thành viên (SV + GV) | Scheduler (8:00 AM) |

---

## 🔄 LUỒNG HOẠT ĐỘNG

### Luồng tạo notification thông thường:

```
1. User thực hiện action (create, update, lock, etc.)
   ↓
2. Service method được gọi
   ↓
3. Thực hiện business logic (save/update database)
   ↓
4. Gọi NotificationHelperService
   ↓
5. Tạo Notification entity và save vào DB
   ↓
6. Gửi WebSocket message realtime
   ↓
7. Update unread count
   ↓
8. User nhận notification qua WebSocket (nếu online)
```

### Luồng deadline notification (Scheduler):

```
1. Scheduler chạy vào 8:00 AM mỗi ngày
   ↓
2. Query database tìm items có endDate = tomorrow
   ↓
3. Filter items chưa hoàn thành
   ↓
4. Với mỗi item:
   - Tạo notification cho toàn bộ thành viên
   - Gửi WebSocket realtime
   - Update unread count
   ↓
5. Log kết quả
```

---

## 🔧 CẤU HÌNH MENTION

### Format mention trong comment:
```
@[userId]
```

### Ví dụ:
```
Xin chào @[123], bạn có thể xem lại task này không? cc: @[456]
```

### Parse logic:
```java
// Regex pattern: @\[(\d+)\]
List<Long> userIds = notificationHelperService.extractMentionedUserIds(commentText);
```

---

## 📱 TÍCH HỢP VỚI WEBSOCKET

Tất cả notifications đều tự động gửi qua WebSocket với cấu trúc:

```json
{
  "id": 123,
  "title": "Bạn được thêm vào dự án",
  "message": "Bạn đã được thêm vào dự án \"Mobile App\"",
  "type": "PROJECT_ASSIGNED",
  "referenceId": 45,
  "referenceType": "PROJECT",
  "triggeredById": 12,
  "triggeredByName": "Nguyễn Văn A",
  "timestamp": "2025-11-29T10:30:00",
  "isRead": false,
  "action": "NEW_NOTIFICATION"
}
```

**WebSocket Topics**:
- `/user/queue/notifications` - Notification messages
- `/user/queue/notification-count` - Unread count updates
- `/user/queue/notification-updates` - Read/delete updates

---

## ✅ CHECKLIST TRIỂN KHAI

### Backend
- [x] Tạo/mở rộng `ENotificationType` enum
- [x] Tạo `NotificationHelperService`
- [x] Tạo `DeadlineNotificationScheduler`
- [x] Enable `@EnableScheduling` trong Application
- [x] Update `ProjectMemberServiceImpl` (PROJECT_ASSIGNED)
- [x] Update `TaskServiceImpl` (TASK_ASSIGNED, TASK_COMPLETED, TASK_LOCKED)
- [x] Update `ReportServiceImpl` (REPORT_SUBMITTED, REPORT_LOCKED)
- [x] Update `CommentServiceImpl` (COMMENT_ADDED, MENTION)
- [x] Update `ProjectServiceImpl` (PROJECT_LOCKED, PROJECT_CONTENT_LOCKED, PROJECT_CONTENT_DEFINED)
- [x] Update `MilestoneServiceImpl` (MILESTONE_LOCKED)
- [x] Thêm repository methods (findByEndDate)
- [x] Tích hợp WebSocket cho realtime notifications

### Frontend (Cần làm tiếp)
- [ ] Tạo file `.env` với Firebase config
- [ ] Uncomment `NotificationManager` trong `App.tsx`
- [ ] Integrate WebSocket vào `Header.tsx`
- [ ] Replace dummy data với real API calls
- [ ] Add notification badge (unread count)
- [ ] Test end-to-end với backend

---

## 🚀 CÁCH SỬ DỤNG

### 1. Development

```bash
# Backend
cd backend
./mvnw spring-boot:run

# Scheduler sẽ tự động chạy lúc 8:00 AM mỗi ngày
# Hoặc test manual bằng cách trigger các actions
```

### 2. Test Deadline Notifications

```java
// Tạo task với endDate = ngày mai
LocalDate tomorrow = LocalDate.now().plusDays(1);
task.setEndDate(tomorrow);
taskRepository.save(task);

// Đợi đến 8:00 AM hoặc trigger manual scheduler
```

### 3. Test Mention Notifications

```json
// POST /api/v1/comments
{
  "taskId": 123,
  "content": "Xin chào @[456], bạn có thể review task này không?"
}
// User 456 sẽ nhận notification MENTION
```

---

## 📝 LƯU Ý

### Error Handling
- Tất cả notification code được wrap trong `try-catch`
- Nếu notification fail, không ảnh hưởng đến business logic chính
- Errors được log nhưng không throw exception

### Performance
- Notifications được tạo async (không block main thread)
- WebSocket gửi non-blocking
- Scheduler chạy 1 lần/ngày (8:00 AM) để tránh overhead

### Security
- Chỉ gửi notification cho members của project
- Instructor notifications vs Student-only notifications được phân biệt rõ ràng
- Triggered by user được track để audit

---

## 🔮 TƯƠNG LAI - KẾ HOẠCH MỞ RỘNG

### Phase 2 (Đề xuất)
- [ ] Email notifications (gửi email kèm WebSocket)
- [ ] Firebase Push Notifications (mobile app)
- [ ] Notification preferences (user có thể chọn loại thông báo muốn nhận)
- [ ] Notification grouping (nhóm nhiều notifications cùng loại)
- [ ] Mark all as read
- [ ] Notification history/archive
- [ ] Customizable scheduler time (không fix 8:00 AM)

### Phase 3 (Đề xuất)
- [ ] Real-time notification count trong navbar
- [ ] Notification sound/vibration
- [ ] Rich notifications (với images, actions)
- [ ] Notification analytics/tracking
- [ ] A/B testing notification messages

---

## 📚 TÀI LIỆU THAM KHẢO

- [WebSocket Documentation](./websocket/INDEX.md)
- [Firebase Push Notification Guide](./firebase/FIREBASE_PUSH_NOTIFICATION_GUIDE.md)
- [Notification Entity](../src/main/java/POSE_Project_Tracking/Blog/entity/Notification.java)
- [WebSocket Service](../src/main/java/POSE_Project_Tracking/Blog/service/WebSocketNotificationService.java)

---

## 👥 CONTRIBUTORS

- Implementation: AI Assistant
- Review: Development Team
- Testing: QA Team

---

## 📞 HỖ TRỢ

Nếu có vấn đề hoặc câu hỏi, vui lòng:
1. Check logs trong console
2. Verify WebSocket connection trong browser DevTools
3. Check database `notifications` table
4. Review scheduler logs (search for "deadline notification")

---

**Last Updated**: 29/11/2025
**Version**: 1.0.0
**Status**: ✅ Ready for Testing
