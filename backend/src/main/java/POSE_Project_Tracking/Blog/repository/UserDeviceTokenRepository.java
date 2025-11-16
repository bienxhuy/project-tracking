package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserDeviceToken entity
 */
@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {

    /**
     * Tìm tất cả device tokens của một user
     */
    List<UserDeviceToken> findByUser(User user);

    /**
     * Tìm tất cả active device tokens của một user
     */
    List<UserDeviceToken> findByUserAndIsActiveTrue(User user);

    /**
     * Tìm device token theo FCM token
     */
    Optional<UserDeviceToken> findByFcmToken(String fcmToken);

    /**
     * Tìm device tokens theo user ID
     */
    List<UserDeviceToken> findByUserId(Long userId);

    /**
     * Tìm active device tokens theo user ID
     */
    List<UserDeviceToken> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Kiểm tra FCM token đã tồn tại chưa
     */
    boolean existsByFcmToken(String fcmToken);

    /**
     * Xóa device token theo FCM token
     */
    void deleteByFcmToken(String fcmToken);
}
