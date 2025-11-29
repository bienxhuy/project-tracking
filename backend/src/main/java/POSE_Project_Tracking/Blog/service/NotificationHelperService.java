package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.PushNotificationRequest;
import POSE_Project_Tracking.Blog.dto.PushNotificationResponse;
import POSE_Project_Tracking.Blog.dto.WebSocketNotificationMessage;
import POSE_Project_Tracking.Blog.entity.Notification;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.UserDeviceToken;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.repository.NotificationRepository;
import POSE_Project_Tracking.Blog.repository.ProjectMemberRepository;
import POSE_Project_Tracking.Blog.repository.UserDeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper service để tạo và gửi notifications cho nhiều users
 */
@Slf4j
@Service
@Transactional
public class NotificationHelperService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    /**
     * Tạo notification cho một user
     */
    public Notification createNotification(
            User user, 
            String title,
            String message, 
            ENotificationType type,
            Long referenceId,
            String referenceType,
            User triggeredBy) {
        
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .triggeredBy(triggeredBy)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        
        // ✅ 1. Send WebSocket notification (cho user đang online)
        try {
            WebSocketNotificationMessage wsMessage = webSocketNotificationService
                .convertToWebSocketMessage(notification, "NEW_NOTIFICATION");
            webSocketNotificationService.sendNotificationToUser(user.getId(), wsMessage);
            
            // Update unread count
            Long unreadCount = notificationRepository.countByUserAndIsRead(user, false);
            webSocketNotificationService.sendNotificationCount(user.getId(), unreadCount);
            log.info("✅ Sent WebSocket notification to user {}: {}", user.getId(), title);
        } catch (Exception e) {
            log.error("❌ Failed to send WebSocket notification: {}", e.getMessage());
        }

        // ✅ 2. Send Firebase Push Notification (cho user offline hoặc app bị đóng)
        try {
            sendFirebasePushNotification(user, notification, title, message, type, referenceId);
        } catch (Exception e) {
            log.error("❌ Failed to send Firebase push notification: {}", e.getMessage());
        }

        return notification;
    }

    /**
     * Tạo notification cho nhiều users
     */
    public void createNotificationsForUsers(
            List<User> users,
            String title,
            String message,
            ENotificationType type,
            Long referenceId,
            String referenceType,
            User triggeredBy) {
        
        for (User user : users) {
            // Không gửi notification cho chính người trigger
            if (triggeredBy != null && user.getId().equals(triggeredBy.getId())) {
                continue;
            }
            
            createNotification(user, title, message, type, referenceId, referenceType, triggeredBy);
        }
    }

    /**
     * Tạo notification cho tất cả thành viên trong project
     */
    public void createNotificationsForProjectMembers(
            Project project,
            String title,
            String message,
            ENotificationType type,
            Long referenceId,
            String referenceType,
            User triggeredBy) {
        
        List<User> members = projectMemberRepository.findByProject(project)
                .stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                .map(member -> member.getUser())
                .collect(Collectors.toList());
        
        createNotificationsForUsers(members, title, message, type, referenceId, referenceType, triggeredBy);
    }

    /**
     * Tạo notification cho tất cả thành viên trong project (bao gồm instructor)
     */
    public void createNotificationsForAllProjectMembers(
            Project project,
            String title,
            String message,
            ENotificationType type,
            Long referenceId,
            String referenceType,
            User triggeredBy) {
        
        List<User> members = projectMemberRepository.findByProject(project)
                .stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                .map(member -> member.getUser())
                .collect(Collectors.toList());
        
        // Thêm instructor vào danh sách
        if (project.getInstructor() != null) {
            members.add(project.getInstructor());
        }
        
        createNotificationsForUsers(members, title, message, type, referenceId, referenceType, triggeredBy);
    }

    /**
     * Tạo notification cho chỉ sinh viên trong project (không gửi cho instructor)
     */
    public void createNotificationsForStudentsOnly(
            Project project,
            String title,
            String message,
            ENotificationType type,
            Long referenceId,
            String referenceType,
            User triggeredBy) {
        
        List<User> students = projectMemberRepository.findByProject(project)
                .stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                .map(member -> member.getUser())
                .collect(Collectors.toList());
        
        createNotificationsForUsers(students, title, message, type, referenceId, referenceType, triggeredBy);
    }

    /**
     * Parse @mentions từ comment text
     * Format: @username hoặc @userId
     */
    public List<Long> extractMentionedUserIds(String text) {
        // Simple implementation - can be enhanced with regex
        // Format: @[userId] hoặc @username
        List<Long> userIds = new java.util.ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return userIds;
        }
        
        // Find all @mentions
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@\\[(\\d+)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            try {
                Long userId = Long.parseLong(matcher.group(1));
                userIds.add(userId);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID in mention: {}", matcher.group(1));
            }
        }
        
        return userIds;
    }

    /**
     * Gửi Firebase Push Notification cho một user
     * Method này được gọi sau khi đã lưu notification vào DB và gửi WebSocket
     */
    private void sendFirebasePushNotification(
            User user,
            Notification notification,
            String title,
            String message,
            ENotificationType type,
            Long referenceId) {
        
        // Lấy tất cả active device tokens của user
        List<UserDeviceToken> deviceTokens = userDeviceTokenRepository.findByUserAndIsActiveTrue(user);
        
        if (deviceTokens.isEmpty()) {
            log.debug("No active device tokens found for user {}", user.getId());
            return;
        }

        // Extract FCM tokens
        List<String> fcmTokens = deviceTokens.stream()
                .map(UserDeviceToken::getFcmToken)
                .collect(Collectors.toList());

        // Prepare notification data
        Map<String, String> data = new HashMap<>();
        data.put("notificationId", notification.getId().toString());
        data.put("type", type.name());
        data.put("referenceId", referenceId != null ? referenceId.toString() : "");
        data.put("referenceType", notification.getReferenceType() != null ? notification.getReferenceType() : "");
        data.put("userId", user.getId().toString());
        data.put("timestamp", notification.getCreatedAt().toString());

        // Build push notification request
        PushNotificationRequest pushRequest = PushNotificationRequest.builder()
                .title(title)
                .body(message)
                .data(data)
                .build();

        // Gửi từng token một thay vì multicast (workaround cho lỗi batch)
        int successCount = 0;
        int failureCount = 0;
        
        for (String fcmToken : fcmTokens) {
            try {
                pushRequest.setToken(fcmToken);
                PushNotificationResponse response = firebaseMessagingService.sendNotificationToToken(pushRequest);
                
                if (response.isSuccess()) {
                    successCount++;
                    log.debug("✅ Sent to token: {}...", fcmToken.substring(0, Math.min(20, fcmToken.length())));
                } else {
                    failureCount++;
                    log.warn("❌ Failed to send to token: {}..., error: {}", 
                            fcmToken.substring(0, Math.min(20, fcmToken.length())), 
                            response.getError());
                    
                    // Tự động xóa token không valid
                    if (response.isTokenInvalid()) {
                        log.warn("⚠️ Removing invalid FCM token from database");
                        userDeviceTokenRepository.deleteByFcmToken(fcmToken);
                    }
                }
            } catch (Exception e) {
                failureCount++;
                log.error("❌ Exception sending to token: {}", e.getMessage());
            }
        }
        
        log.info("✅ Firebase push notification summary for user {}: {} success, {} failed", 
                user.getId(), successCount, failureCount);
    }

    /**
     * Xử lý các FCM tokens không còn valid (expired hoặc unregistered)
     */
    private void handleInvalidTokens(Exception exception, List<String> tokens) {
        // TODO: Parse exception để tìm invalid tokens và xóa khỏi DB
        // FirebaseMessaging trả về BatchResponse với danh sách failures
        // Có thể implement logic để cleanup invalid tokens
        log.debug("Handling invalid tokens (if any)");
    }
}
