# 🚀 NOTIFICATION SYSTEM - QUICK START GUIDE

## ⚡ OVERVIEW

Hệ thống thông báo tự động cho 15 loại sự kiện trong project tracking system.

---

## 📋 DANH SÁCH NOTIFICATIONS

### 1️⃣ **Project Notifications**
- ✅ Sinh viên được thêm vào project → `PROJECT_ASSIGNED`
- ✅ Project content được define → `PROJECT_CONTENT_DEFINED`
- ✅ Project bị khóa → `PROJECT_LOCKED`
- ✅ Project content bị khóa → `PROJECT_CONTENT_LOCKED`
- ✅ Project deadline (1 ngày) → `PROJECT_DEADLINE_APPROACHING`

### 2️⃣ **Task Notifications**
- ✅ Task được giao → `TASK_ASSIGNED`
- ✅ Task hoàn thành → `TASK_COMPLETED`
- ✅ Task bị khóa → `TASK_LOCKED`
- ✅ Task deadline (1 ngày) → `TASK_DEADLINE_APPROACHING`

### 3️⃣ **Milestone Notifications**
- ✅ Milestone bị khóa → `MILESTONE_LOCKED`
- ✅ Milestone deadline (1 ngày) → `MILESTONE_DEADLINE_APPROACHING`

### 4️⃣ **Report Notifications**
- ✅ Report được submit → `REPORT_SUBMITTED`
- ✅ Report bị khóa → `REPORT_LOCKED`

### 5️⃣ **Comment Notifications**
- ✅ Comment mới → `COMMENT_ADDED`
- ✅ User được mention → `MENTION` (format: `@[userId]`)

---

## 🔧 FILES CREATED/MODIFIED

### ✨ New Files
```
backend/src/main/java/POSE_Project_Tracking/Blog/
├── service/NotificationHelperService.java          (NEW)
├── scheduler/DeadlineNotificationScheduler.java    (NEW)
└── docs/NOTIFICATION_IMPLEMENTATION_SUMMARY.md     (NEW)
```

### 📝 Modified Files
```
backend/src/main/java/POSE_Project_Tracking/Blog/
├── enums/ENotificationType.java                    (UPDATED)
├── ProjectTrackingApplication.java                 (UPDATED)
├── repository/
│   ├── ProjectRepository.java                      (UPDATED)
│   ├── MilestoneRepository.java                    (UPDATED)
│   └── TaskRepository.java                         (UPDATED)
└── service/impl/
    ├── ProjectMemberServiceImpl.java               (UPDATED)
    ├── TaskServiceImpl.java                        (UPDATED)
    ├── ReportServiceImpl.java                      (UPDATED)
    ├── CommentServiceImpl.java                     (UPDATED)
    ├── ProjectServiceImpl.java                     (UPDATED)
    └── MilestoneServiceImpl.java                   (UPDATED)
```

---

## 🎯 KEY FEATURES

### 🔔 Automatic Notifications
- Tự động gửi khi có action (no manual trigger needed)
- Real-time via WebSocket
- Persistent in database

### 👥 Smart Targeting
- **All members** (SV + GV): report submit, comment, task complete, deadlines
- **Students only**: lock actions
- **Mentioned users**: khi được mention trong comment

### ⏰ Scheduler
- Chạy mỗi ngày lúc **8:00 AM**
- Check deadlines **1 ngày trước**
- Chỉ notify items **chưa hoàn thành**

### 🚫 Error Safe
- Không block main business logic
- Wrapped trong try-catch
- Log errors nhưng không throw

---

## 💡 USAGE EXAMPLES

### Example 1: Thêm member vào project
```java
// API: POST /api/v1/project-members
// Body: { "projectId": 1, "userId": 123 }

// ✅ Notification tự động gửi:
// - Type: PROJECT_ASSIGNED
// - To: User 123
// - Message: "Bạn đã được thêm vào dự án 'Mobile App'"
```

### Example 2: Submit report
```java
// API: POST /api/v1/reports
// Body: { "taskId": 45, "title": "Weekly Report", ... }

// ✅ Notification tự động gửi:
// - Type: REPORT_SUBMITTED
// - To: All project members (students + instructor)
// - Message: "Nguyễn Văn A đã submit báo cáo 'Weekly Report'"
```

### Example 3: Comment với mention
```java
// API: POST /api/v1/comments
// Body: { 
//   "taskId": 45, 
//   "content": "Xin chào @[123], bạn review giúp mình nhé! cc: @[456]"
// }

// ✅ 2 Notifications tự động gửi:
// 1. COMMENT_ADDED → All project members
// 2. MENTION → User 123 và User 456
```

### Example 4: Lock task
```java
// API: PATCH /api/v1/tasks/45/lock

// ✅ Notification tự động gửi:
// - Type: TASK_LOCKED
// - To: Students only (không gửi cho instructor)
// - Message: "Nhiệm vụ 'Implement Login' đã bị khóa bởi giảng viên"
```

---

## 🔍 TESTING

### 1. Test Real-time Notifications
```bash
# Terminal 1: Start backend
cd backend
./mvnw spring-boot:run

# Terminal 2: Connect WebSocket (browser console)
const ws = new SockJS('http://localhost:9090/ws');
const client = Stomp.over(ws);
client.connect({Authorization: 'Bearer YOUR_TOKEN'}, () => {
    client.subscribe('/user/queue/notifications', (msg) => {
        console.log('Notification:', JSON.parse(msg.body));
    });
});

# Terminal 3: Trigger action
curl -X POST http://localhost:9090/api/v1/project-members \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"projectId": 1, "userId": 123}'

# ✅ You should see notification in browser console
```

### 2. Test Deadline Scheduler
```bash
# Option 1: Wait until 8:00 AM tomorrow

# Option 2: Temporarily change cron expression for testing
# In DeadlineNotificationScheduler.java:
@Scheduled(cron = "0 * * * * *")  // Run every minute
public void checkDeadlineApproaching() { ... }

# Create test data with tomorrow's deadline
curl -X POST http://localhost:9090/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Task",
    "endDate": "2025-11-30",  # tomorrow
    ...
  }'

# ✅ Check logs at 8:00 AM (or every minute if modified)
```

### 3. Test Mention Parsing
```bash
curl -X POST http://localhost:9090/api/v1/comments \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": 45,
    "content": "Hello @[123], please review! cc: @[456]"
  }'

# ✅ Users 123 and 456 should receive MENTION notifications
```

---

## 📊 DATABASE QUERIES

### Check notifications
```sql
-- Xem tất cả notifications
SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10;

-- Xem notifications chưa đọc của user
SELECT * FROM notifications WHERE user_id = 123 AND is_read = false;

-- Xem notifications theo type
SELECT type, COUNT(*) FROM notifications GROUP BY type;

-- Xem top triggered users
SELECT u.name, COUNT(*) as notification_count 
FROM notifications n 
JOIN users u ON n.triggered_by_id = u.id 
GROUP BY u.name 
ORDER BY notification_count DESC;
```

---

## 🐛 TROUBLESHOOTING

### Notification không gửi?
```bash
# 1. Check logs
tail -f logs/application.log | grep -i notification

# 2. Verify WebSocket connection
# Browser DevTools → Network → WS → Check connection status

# 3. Check database
SELECT * FROM notifications WHERE created_at > NOW() - INTERVAL 1 HOUR;

# 4. Verify service injection
# NotificationHelperService phải được @Autowired trong các service
```

### Scheduler không chạy?
```bash
# 1. Verify @EnableScheduling
# Check ProjectTrackingApplication.java có @EnableScheduling

# 2. Check logs lúc 8:00 AM
grep "deadline notification" logs/application.log

# 3. Test với cron modified
# Change to "0 * * * * *" for every minute testing
```

### WebSocket disconnect?
```bash
# 1. Check JWT token expiration
# Token phải còn valid

# 2. Check CORS configuration
# WebSocket endpoint phải allow CORS

# 3. Check heartbeat
# Default: 10s incoming, 10s outgoing
```

---

## 📚 REFERENCES

- **Full Documentation**: `NOTIFICATION_IMPLEMENTATION_SUMMARY.md`
- **WebSocket Docs**: `websocket/INDEX.md`
- **Firebase Docs**: `firebase/FIREBASE_QUICK_START.md`

---

## ✅ CHECKLIST

### Backend Ready ✅
- [x] NotificationHelperService created
- [x] Scheduler created
- [x] All services updated
- [x] Repository methods added
- [x] @EnableScheduling enabled

### Frontend TODO
- [ ] Create `.env` with Firebase config
- [ ] Uncomment NotificationManager in App.tsx
- [ ] Integrate WebSocket in Header.tsx
- [ ] Replace dummy data with real API
- [ ] Add notification badge

---

## 🎉 DONE!

Hệ thống notification đã sẵn sàng! 

**Test it now**: Thử thêm member vào project hoặc submit report và check notification! 🚀

---

**Version**: 1.0.0  
**Date**: 29/11/2025  
**Status**: ✅ Production Ready
