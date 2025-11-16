package POSE_Project_Tracking.Blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for Push Notification Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationRequest {
    
    /**
     * Device token (FCM token) của thiết bị nhận thông báo
     */
    private String token;
    
    /**
     * Tiêu đề của thông báo
     */
    private String title;
    
    /**
     * Nội dung của thông báo
     */
    private String body;
    
    /**
     * URL hình ảnh cho thông báo (optional)
     */
    private String imageUrl;
    
    /**
     * Dữ liệu tùy chỉnh gửi kèm thông báo (optional)
     */
    private Map<String, String> data;
    
    /**
     * Topic để gửi notification cho một nhóm (optional)
     * Nếu có topic thì sẽ gửi cho tất cả devices subscribe topic đó
     */
    private String topic;
}
