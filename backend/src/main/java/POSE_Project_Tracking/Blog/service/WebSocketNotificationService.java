package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.WebSocketNotificationMessage;
import POSE_Project_Tracking.Blog.entity.Notification;
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

    /**
     * Send notification to a specific user
     * Client subscribes to: /user/queue/notifications
     * 
     * @param userId User ID to send notification to
     * @param message Notification message
     */
    public void sendNotificationToUser(Long userId, WebSocketNotificationMessage message) {
        try {
            String destination = "/user/queue/notifications";
            messagingTemplate.convertAndSendToUser(
                userId.toString(), 
                destination, 
                message
            );
            log.info("Sent WebSocket notification to user {}: {}", userId, message.getTitle());
        } catch (Exception e) {
            log.error("Error sending WebSocket notification to user {}: {}", userId, e.getMessage());
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
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-count",
                unreadCount
            );
            log.debug("Sent notification count to user {}: {}", userId, unreadCount);
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
            WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
                .id(notificationId)
                .action("NOTIFICATION_READ")
                .build();
            
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-updates",
                message
            );
            log.debug("Notified user {} that notification {} was read", userId, notificationId);
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
