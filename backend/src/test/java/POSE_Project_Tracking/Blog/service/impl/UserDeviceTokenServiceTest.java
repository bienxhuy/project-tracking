package POSE_Project_Tracking.Blog.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.UserDeviceToken;
import POSE_Project_Tracking.Blog.repository.UserDeviceTokenRepository;
import POSE_Project_Tracking.Blog.service.UserDeviceTokenService;

@ExtendWith(MockitoExtension.class)
class UserDeviceTokenServiceTest {

    private static final String FCM_TOKEN = "test-token";
    private static final String DEVICE_TYPE = "ANDROID";
    private static final String DEVICE_INFO = "Pixel 7";

    @Mock
    private UserDeviceTokenRepository deviceTokenRepository;

    @InjectMocks
    private UserDeviceTokenService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(42L);
    }

    @Test
    void saveDeviceToken_existingToken_updatesFields() {
        User oldOwner = new User();
        oldOwner.setId(99L);

        UserDeviceToken existingToken = UserDeviceToken.builder()
                .fcmToken(FCM_TOKEN)
                .deviceType("OLD")
                .deviceInfo("Old info")
                .user(oldOwner)
                .isActive(false)
                .lastUsedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(deviceTokenRepository.findByFcmToken(FCM_TOKEN)).thenReturn(Optional.of(existingToken));
        when(deviceTokenRepository.save(any(UserDeviceToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDeviceToken updated = service.saveDeviceToken(user, FCM_TOKEN, DEVICE_TYPE, DEVICE_INFO);

        assertEquals(user, updated.getUser());
        assertEquals(DEVICE_TYPE, updated.getDeviceType());
        assertEquals(DEVICE_INFO, updated.getDeviceInfo());
        assertTrue(updated.getIsActive());
        assertNotNull(updated.getLastUsedAt());
        verify(deviceTokenRepository).save(updated);
    }

    @Test
    void getActiveFcmTokens_user_returnsTokenStrings() {
        List<UserDeviceToken> tokens = List.of(
                buildToken("token-1"),
                buildToken("token-2")
        );

        when(deviceTokenRepository.findByUserAndIsActiveTrue(user)).thenReturn(tokens);

        List<String> actual = service.getActiveFcmTokens(user);

        assertEquals(List.of("token-1", "token-2"), actual);
        verify(deviceTokenRepository).findByUserAndIsActiveTrue(user);
    }

    @Test
    void deactivateDeviceToken_existingToken_setsInactive() {
        UserDeviceToken token = buildToken(FCM_TOKEN);

        when(deviceTokenRepository.findByFcmToken(FCM_TOKEN)).thenReturn(Optional.of(token));
        when(deviceTokenRepository.save(any(UserDeviceToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.deactivateDeviceToken(FCM_TOKEN);

        ArgumentCaptor<UserDeviceToken> tokenCaptor = ArgumentCaptor.forClass(UserDeviceToken.class);
        verify(deviceTokenRepository).save(tokenCaptor.capture());

        assertFalse(tokenCaptor.getValue().getIsActive());
    }

    @Test
    void deleteAllUserDeviceTokens_user_deletesTokens() {
        List<UserDeviceToken> tokens = List.of(buildToken("token-1"), buildToken("token-2"));

        when(deviceTokenRepository.findByUser(user)).thenReturn(tokens);

        service.deleteAllUserDeviceTokens(user);

        verify(deviceTokenRepository).deleteAll(tokens);
    }

    @Test
    void deactivateAllUserDeviceTokens_user_setsAllInactiveAndSaves() {
        List<UserDeviceToken> tokens = List.of(buildToken("token-1"), buildToken("token-2"));

        when(deviceTokenRepository.findByUser(user)).thenReturn(tokens);
        when(deviceTokenRepository.saveAll(tokens)).thenReturn(tokens);

        service.deactivateAllUserDeviceTokens(user);

        assertTrue(tokens.stream().allMatch(token -> Boolean.FALSE.equals(token.getIsActive())));
        verify(deviceTokenRepository).saveAll(tokens);
    }

    @Test
    void updateLastUsedTime_existingToken_updatesTimestamp() {
        UserDeviceToken token = buildToken(FCM_TOKEN);
        LocalDateTime beforeUpdate = LocalDateTime.now().minusDays(1);
        token.setLastUsedAt(beforeUpdate);

        when(deviceTokenRepository.findByFcmToken(FCM_TOKEN)).thenReturn(Optional.of(token));
        when(deviceTokenRepository.save(any(UserDeviceToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateLastUsedTime(FCM_TOKEN);

        ArgumentCaptor<UserDeviceToken> tokenCaptor = ArgumentCaptor.forClass(UserDeviceToken.class);
        verify(deviceTokenRepository).save(tokenCaptor.capture());

        assertTrue(tokenCaptor.getValue().getLastUsedAt().isAfter(beforeUpdate));
    }

    @Test
    void deleteDeviceToken_token_invokesRepository() {
        service.deleteDeviceToken(FCM_TOKEN);

        verify(deviceTokenRepository).deleteByFcmToken(FCM_TOKEN);
    }

    private UserDeviceToken buildToken(String tokenValue) {
        return UserDeviceToken.builder()
                .fcmToken(tokenValue)
                .deviceType(DEVICE_TYPE)
                .deviceInfo(DEVICE_INFO)
                .user(user)
                .isActive(true)
                .build();
    }
}

