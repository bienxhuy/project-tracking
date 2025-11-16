package POSE_Project_Tracking.Blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity để lưu FCM device tokens của users
 */
@Entity
@Table(name = "user_device_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User sở hữu device này
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * FCM Token của device
     */
    @Column(nullable = false, unique = true, length = 500)
    private String fcmToken;

    /**
     * Loại device: ANDROID, IOS, WEB
     */
    @Column(nullable = false, length = 20)
    private String deviceType;

    /**
     * Thông tin device (optional): model, OS version, etc.
     */
    @Column(length = 500)
    private String deviceInfo;

    /**
     * Trạng thái hoạt động của token
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Thời gian tạo token
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Lần cuối token được sử dụng
     */
    @Column
    private LocalDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
