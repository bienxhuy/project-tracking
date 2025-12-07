package POSE_Project_Tracking.Blog.facade;

import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.res.MessageDTO;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.OTP;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.service.IEmailService;
import POSE_Project_Tracking.Blog.service.IOTPService;
import POSE_Project_Tracking.Blog.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserOTPFacadeTest {

    @Mock
    private IUserService userService;

    @Mock
    private IOTPService otpService;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private UserOTPFacade facade;

    private UserReq userReq;
    private UserRes userRes;
    private OTP otp;

    @BeforeEach
    void setUp() {
        userReq = UserReq.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .displayName("Test User")
                .build();

        userRes = new UserRes();
        userRes.setId(1L);
        userRes.setUsername("testuser");
        userRes.setEmail("test@example.com");
        userRes.setRole(EUserRole.STUDENT);

        User user = new User();
        user.setId(1L);
        
        otp = new OTP();
        otp.setId(1L);
        otp.setOtp("123456");
        otp.setUser(user);
    }

    @Test
    void createUser_validRequest_createsUserAndSendsOTP() {
        when(userService.createUser(any(UserReq.class))).thenReturn(userRes);
        // Không cần mock otpService.generateOTP nữa
        doNothing().when(emailService).sendEmail(any(MessageDTO.class));

        UserRes result = facade.createUser(userReq);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).createUser(any(UserReq.class));
        // Không verify otpService.generateOTP nữa vì createUser không gọi nó
        verify(otpService, never()).generateOTP(anyLong());
        verify(emailService).sendEmail(any(MessageDTO.class));
    }

    @Test
    void createUser_emailSendFails_throwsException() {
        when(userService.createUser(any(UserReq.class))).thenReturn(userRes);
        // Không cần mock otpService.generateOTP nữa vì createUser không gọi nó
        doThrow(new RuntimeException("Email service error")).when(emailService).sendEmail(any(MessageDTO.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            facade.createUser(userReq);
        });

        // Đổi expected message theo implementation mới
        assertEquals("Không thể gửi email thông tin tài khoản, vui lòng thử lại", exception.getMessage());
        verify(userService).createUser(any(UserReq.class));
        // Không verify otpService.generateOTP nữa vì không được gọi
        verify(otpService, never()).generateOTP(anyLong());
    }

    @Test
    void generateOTP_validUserId_generatesAndSendsOTP() {
        when(otpService.generateOTP(1L)).thenReturn(otp);
        when(userService.getUserById(1L)).thenReturn(userRes);
        doNothing().when(emailService).sendEmail(any(MessageDTO.class));

        facade.generateOTP(1L);

        verify(otpService).generateOTP(1L);
        verify(userService).getUserById(1L);
        verify(emailService).sendEmail(any(MessageDTO.class));
    }

    @Test
    void generateOTPWithEmail_validEmail_generatesAndSendsOTP() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(userRes);
        when(otpService.generateOTP(1L)).thenReturn(otp);
        doNothing().when(emailService).sendEmail(any(MessageDTO.class));

        facade.generateOTPWithEmail("test@example.com");

        verify(userService).getUserByEmail("test@example.com");
        verify(otpService).generateOTP(1L);
        verify(emailService).sendEmail(any(MessageDTO.class));
    }

    @Test
    void generateOTPForPassword_validEmail_generatesPasswordResetOTP() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(userRes);
        when(otpService.generateOTPForgotPassword(1L)).thenReturn(otp);
        doNothing().when(emailService).sendEmail(any(MessageDTO.class));

        facade.generateOTPForPassword("test@example.com");

        verify(userService).getUserByEmail("test@example.com");
        verify(otpService).generateOTPForgotPassword(1L);
        verify(emailService).sendEmail(any(MessageDTO.class));
    }

    @Test
    void verifyOTP_validOTPWithUserId_activatesUser() {
        when(otpService.verifyOTP(1L, "123456", "test@example.com")).thenReturn(true);

        boolean result = facade.verifyOTP(1L, "123456", "test@example.com");

        assertTrue(result);
        verify(otpService).verifyOTP(1L, "123456", "test@example.com");
    }

    @Test
    void verifyOTP_validOTPWithoutUserId_activatesUserByEmail() {
        when(otpService.verifyOTP(null, "123456", "test@example.com")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(userRes);

        boolean result = facade.verifyOTP(null, "123456", "test@example.com");

        assertTrue(result);
        verify(otpService).verifyOTP(null, "123456", "test@example.com");
        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void verifyOTP_invalidOTP_throwsException() {
        when(otpService.verifyOTP(1L, "wrong", "test@example.com")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            facade.verifyOTP(1L, "wrong", "test@example.com");
        });

        assertEquals("OTP verification failed", exception.getMessage());
        verify(otpService).verifyOTP(1L, "wrong", "test@example.com");
    }
}

