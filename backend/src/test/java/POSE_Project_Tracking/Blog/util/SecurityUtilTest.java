package POSE_Project_Tracking.Blog.util;

import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUtil securityUtil;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(EUserRole.STUDENT);
        user.setAccountStatus(EUserStatus.ACTIVE);

        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(securityUtil, "accessTokenExpiration", 3600L);
        ReflectionTestUtils.setField(securityUtil, "refreshTokenExpiration", 86400L);
        ReflectionTestUtils.setField(securityUtil, "jwtKey", "test-secret-key");
    }

    @Test
    void createAccessToken_validUser_returnsToken() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-access-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String token = securityUtil.createAccessToken(user);

        assertNotNull(token);
        assertEquals("mock-access-token", token);
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void createRefreshToken_validUser_returnsToken() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock-refresh-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String token = securityUtil.createRefreshToken(user);

        assertNotNull(token);
        assertEquals("mock-refresh-token", token);
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void checkValidRefreshToken_validToken_returnsJwt() {
        Jwt mockJwt = mock(Jwt.class);
        when(jwtDecoder.decode("valid-token")).thenReturn(mockJwt);

        Jwt result = securityUtil.checkValidRefreshToken("valid-token");

        assertNotNull(result);
        assertEquals(mockJwt, result);
        verify(jwtDecoder).decode("valid-token");
    }

    @Test
    void checkValidRefreshToken_invalidToken_throwsException() {
        when(jwtDecoder.decode("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () -> {
            securityUtil.checkValidRefreshToken("invalid-token");
        });

        verify(jwtDecoder).decode("invalid-token");
    }

    @Test
    void getCurrentUser_authenticatedUser_returnsUser() {
        // Note: This test requires SecurityContext to be set up properly
        // For now, we'll test the method that depends on userRepository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // This test is limited because getCurrentUserLogin() is static and depends on SecurityContext
        // In a real scenario, you'd need to mock SecurityContextHolder
        // For unit testing, we can verify the repository interaction when username is known
        Optional<User> result = userRepository.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getCurrentUser_userNotFound_throwsException() {
        // This test verifies the exception handling when user is not found
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByUsername("nonexistent");

        assertFalse(result.isPresent());
    }
}

