package POSE_Project_Tracking.Blog.dto;

import POSE_Project_Tracking.Blog.enums.ENotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket notification message DTO
 * Sent to connected clients for real-time updates
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotificationMessage {
    
    /**
     * Notification ID
     */
    private Long id;
    
    /**
     * Notification title
     */
    private String title;
    
    /**
     * Notification message/body
     */
    private String message;
    
    /**
     * Notification type
     */
    private ENotificationType type;
    
    /**
     * Reference ID (e.g., taskId, projectId)
     */
    private Long referenceId;
    
    /**
     * Reference type (e.g., "TASK", "PROJECT", "COMMENT")
     */
    private String referenceType;
    
    /**
     * User who triggered this notification
     */
    private Long triggeredById;
    
    /**
     * Name of user who triggered this notification
     */
    private String triggeredByName;
    
    /**
     * Timestamp when notification was created
     */
    private LocalDateTime timestamp;
    
    /**
     * Is this notification read?
     */
    private Boolean isRead;
    
    /**
     * Action type for client-side handling
     * e.g., "NEW_NOTIFICATION", "NOTIFICATION_READ", "NOTIFICATION_DELETED"
     */
    private String action;
}
