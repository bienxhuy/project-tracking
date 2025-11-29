package POSE_Project_Tracking.Blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Push Notification Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationResponse {
    
    /**
     * Trạng thái gửi thông báo
     */
    private boolean success;
    
    /**
     * Message ID từ Firebase (nếu gửi thành công)
     */
    private String messageId;
    
    /**
     * Thông báo lỗi (nếu có)
     */
    private String error;
    
    /**
     * FCM Error Code (UNREGISTERED, INVALID_ARGUMENT, etc.)
     */
    private String fcmErrorCode;
    
    /**
     * Token có bị invalid không (cần xóa khỏi DB)
     */
    private boolean tokenInvalid;
}
