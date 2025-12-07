package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.WebSocketNotificationMessage;
import POSE_Project_Tracking.Blog.entity.Notification;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending WebSocket notifications to connected clients
 * Integrates with Firebase FCM for offline users
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final org.springframework.messaging.simp.user.SimpUserRegistry simpUserRegistry;

    /**
     * Send notification to a specific user
     * Client subscribes to: /user/queue/notifications
     * 
     * @param userId User ID to send notification to
     * @param message Notification message
     */
    public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
        try {
            // Lookup username from userId
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("⚠️ User not found for userId: {}", userId);
                return;
            }
            
            String username = user.getUsername();
            String destination = "/queue/notifications";
            
            log.info("🔍 Sending notification to userId={}, username='{}' (length: {})", 
                userId, username, username.length());
            
            // Debug: Check all active users
            log.info("📋 Active WebSocket users:");
            simpUserRegistry.getUsers().forEach(simpUser -> {
                log.info("  - User: '{}' (length: {}), Sessions: {}", 
                    simpUser.getName(), 
                    simpUser.getName().length(), 
                    simpUser.getSessions().size());
            });
            
            // Check if target user exists in registry
            org.springframework.messaging.simp.user.SimpUser targetUser = simpUserRegistry.getUser(username);
            if (targetUser == null) {
                log.warn("⚠️ User '{}' NOT found in SimpUserRegistry!", username);
                log.warn("   Available users: {}", 
                    simpUserRegistry.getUsers().stream()
                        .map(org.springframework.messaging.simp.user.SimpUser::getName)
                        .toList());
            } else {
                log.info("✅ User '{}' found in registry with {} session(s)", 
                    username, targetUser.getSessions().size());
            }
            
            messagingTemplate.convertAndSendToUser(
                username,  // Use username instead of userId
                destination, 
                message
            );
            log.info("✅ Sent WebSocket notification to user {} ({}): {}", userId, username, message.getTitle());
        } catch (Exception e) {
            log.error("❌ Error sending WebSocket notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Broadcast notification to all connected users
     * Client subscribes to: /topic/notifications
     * 
     * @param message Notification message
     */
    public void broadcastNotification(WebSocketNotificationMessage message) {
        try {
            messagingTemplate.convertAndSend("/topic/notifications", message);
            log.info("Broadcasted WebSocket notification: {}", message.getTitle());
        } catch (Exception e) {
            log.error("Error broadcasting WebSocket notification: {}", e.getMessage());
        }
    }

    /**
     * Send notification count update to user
     * Client subscribes to: /user/queue/notification-count
     * 
     * @param userId User ID
     * @param unreadCount Number of unread notifications
     */
    public void sendNotificationCount(Long userId, Long unreadCount) {
        try {
            // Lookup username from userId
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("⚠️ User not found for userId: {}", userId);
                return;
            }
            
            String username = user.getUsername();
            
            messagingTemplate.convertAndSendToUser(
                username,  // Use username instead of userId
                "/queue/notification-count",
                unreadCount
            );
            log.debug("Sent notification count to user {} ({}): {}", userId, username, unreadCount);
        } catch (Exception e) {
            log.error("Error sending notification count to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Notify user that a notification was marked as read
     * Client subscribes to: /user/queue/notification-read
     * 
     * @param userId User ID
     * @param notificationId Notification ID that was read
     */
    public void notifyNotificationRead(Long userId, Long notificationId) {
        try {
            // Lookup username from userId
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("⚠️ User not found for userId: {}", userId);
                return;
            }
            
            String username = user.getUsername();
            
            WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
                .id(notificationId)
                .action("NOTIFICATION_READ")
                .build();
            
            messagingTemplate.convertAndSendToUser(
                username,  // Use username instead of userId
                "/queue/notification-updates",
                message
            );
            log.debug("Notified user {} ({}) that notification {} was read", userId, username, notificationId);
        } catch (Exception e) {
            log.error("Error notifying read status to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Convert Notification entity to WebSocket message
     * 
     * @param notification Notification entity
     * @param action Action type (NEW_NOTIFICATION, NOTIFICATION_READ, etc.)
     * @return WebSocketNotificationMessage
     */
    public WebSocketNotificationMessage convertToWebSocketMessage(Notification notification, String action) {
        return WebSocketNotificationMessage.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .type(notification.getType())
            .referenceId(notification.getReferenceId())
            .referenceType(notification.getReferenceType())
            .triggeredById(notification.getTriggeredBy() != null ? 
                notification.getTriggeredBy().getId() : null)
            .triggeredByName(notification.getTriggeredBy() != null ? 
                notification.getTriggeredBy().getDisplayName() : null)
            .createdAt(notification.getCreatedAt())
            .isRead(notification.getIsRead())
            .action(action)
            .build();
    }
}
