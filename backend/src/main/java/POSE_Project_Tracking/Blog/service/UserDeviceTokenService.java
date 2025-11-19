package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.UserDeviceToken;
import POSE_Project_Tracking.Blog.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service quản lý User Device Tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {

    private final UserDeviceTokenRepository deviceTokenRepository;

    /**
     * Lưu hoặc cập nhật device token
     */
    @Transactional
    public UserDeviceToken saveDeviceToken(User user, String fcmToken, 
                                          String deviceType, String deviceInfo) {
        
        // Kiểm tra token đã tồn tại chưa
        Optional<UserDeviceToken> existingToken = deviceTokenRepository.findByFcmToken(fcmToken);
        
        if (existingToken.isPresent()) {
            // Cập nhật token hiện tại
            UserDeviceToken token = existingToken.get();
            token.setUser(user);
            token.setDeviceType(deviceType);
            token.setDeviceInfo(deviceInfo);
            token.setIsActive(true);
            token.setLastUsedAt(LocalDateTime.now());
            
            log.info("Updated existing device token for user: {}", user.getId());
            return deviceTokenRepository.save(token);
        }
        
        // Tạo token mới
        UserDeviceToken newToken = UserDeviceToken.builder()
                .user(user)
                .fcmToken(fcmToken)
                .deviceType(deviceType)
                .deviceInfo(deviceInfo)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .build();
        
        log.info("Created new device token for user: {}", user.getId());
        return deviceTokenRepository.save(newToken);
    }

    /**
     * Lấy tất cả active device tokens của user
     */
    public List<UserDeviceToken> getActiveDeviceTokens(User user) {
        return deviceTokenRepository.findByUserAndIsActiveTrue(user);
    }

    /**
     * Lấy tất cả active device tokens của user theo userId
     */
    public List<UserDeviceToken> getActiveDeviceTokens(Long userId) {
        return deviceTokenRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Lấy tất cả active FCM token strings của user
     */
    public List<String> getActiveFcmTokens(User user) {
        return getActiveDeviceTokens(user).stream()
                .map(UserDeviceToken::getFcmToken)
                .toList();
    }

    /**
     * Lấy tất cả active FCM token strings của user theo userId
     */
    public List<String> getActiveFcmTokens(Long userId) {
        return getActiveDeviceTokens(userId).stream()
                .map(UserDeviceToken::getFcmToken)
                .toList();
    }

    /**
     * Vô hiệu hóa một device token
     */
    @Transactional
    public void deactivateDeviceToken(String fcmToken) {
        deviceTokenRepository.findByFcmToken(fcmToken).ifPresent(token -> {
            token.setIsActive(false);
            deviceTokenRepository.save(token);
            log.info("Deactivated device token: {}", fcmToken);
        });
    }

    /**
     * Xóa một device token
     */
    @Transactional
    public void deleteDeviceToken(String fcmToken) {
        deviceTokenRepository.deleteByFcmToken(fcmToken);
        log.info("Deleted device token: {}", fcmToken);
    }

    /**
     * Xóa tất cả device tokens của user (khi logout all devices)
     */
    @Transactional
    public void deleteAllUserDeviceTokens(User user) {
        List<UserDeviceToken> tokens = deviceTokenRepository.findByUser(user);
        deviceTokenRepository.deleteAll(tokens);
        log.info("Deleted all device tokens for user: {}", user.getId());
    }

    /**
     * Vô hiệu hóa tất cả device tokens của user
     */
    @Transactional
    public void deactivateAllUserDeviceTokens(User user) {
        List<UserDeviceToken> tokens = deviceTokenRepository.findByUser(user);
        tokens.forEach(token -> token.setIsActive(false));
        deviceTokenRepository.saveAll(tokens);
        log.info("Deactivated all device tokens for user: {}", user.getId());
    }

    /**
     * Cập nhật last used time cho device token
     */
    @Transactional
    public void updateLastUsedTime(String fcmToken) {
        deviceTokenRepository.findByFcmToken(fcmToken).ifPresent(token -> {
            token.setLastUsedAt(LocalDateTime.now());
            deviceTokenRepository.save(token);
        });
    }
}
