package POSE_Project_Tracking.Blog.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import POSE_Project_Tracking.Blog.entity.OTP;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.repository.OTPRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OTPServiceImplTest {

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OTPServiceImpl service;

    private User user;
    private OTP otp;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setAccountStatus(EUserStatus.VERIFYING);

        otp = new OTP();
        otp.setId(1L);
        otp.setUser(user);
        otp.setOtp("123456");
    }

    @Test
    void generateOTP_verifyingUser_generatesOTP() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(otpRepository.save(any(OTP.class))).thenReturn(otp);

        OTP result = service.generateOTP(1L);

        assertNotNull(result);
        verify(otpRepository).save(any(OTP.class));
    }

    @Test
    void generateOTP_userNotVerifying_throwsException() {
        user.setAccountStatus(EUserStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.generateOTP(1L));
    }

    @Test
    void generateOTP_userNotFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.generateOTP(999L));
    }

    @Test
    void generateOTP_existingOTP_deletesOldAndCreatesNew() {
        OTP oldOTP = new OTP();
        oldOTP.setId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.of(oldOTP));
        when(otpRepository.save(any(OTP.class))).thenReturn(otp);

        OTP result = service.generateOTP(1L);

        assertNotNull(result);
        verify(otpRepository).delete(oldOTP);
        verify(otpRepository).save(any(OTP.class));
    }

    @Test
    void generateOTPForgotPassword_validUser_generatesOTP() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(otpRepository.save(any(OTP.class))).thenReturn(otp);

        OTP result = service.generateOTPForgotPassword(1L);

        assertNotNull(result);
        verify(otpRepository).save(any(OTP.class));
    }

    @Test
    void verifyOTP_correctOTP_returnsTrue() {
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.of(otp));

        boolean result = service.verifyOTP(1L, "123456", null);

        assertTrue(result);
        verify(otpRepository).deleteById(1L);
    }

    @Test
    void verifyOTP_incorrectOTP_returnsFalse() {
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.of(otp));

        boolean result = service.verifyOTP(1L, "wrong", null);

        assertFalse(result);
    }

    @Test
    void verifyOTP_byEmail_correctOTP_returnsTrue() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.of(otp));

        boolean result = service.verifyOTP(null, "123456", "test@example.com");

        assertTrue(result);
        verify(otpRepository).deleteById(1L);
    }

    @Test
    void verifyOTP_byEmail_userNotFound_throwsException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> service.verifyOTP(null, "123456", "unknown@example.com"));
    }

    @Test
    void deleteOTP_existingOTP_deletesOTP() {
        when(otpRepository.existsById(1L)).thenReturn(true);

        service.deleteOTP(1L);

        verify(otpRepository).deleteById(1L);
    }

    @Test
    void deleteOTP_nonExistingOTP_throwsException() {
        when(otpRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.deleteOTP(999L));
    }

    @Test
    void getOTPByUserId_existingOTP_returnsOTP() {
        when(otpRepository.findByUserId(1L)).thenReturn(Optional.of(otp));

        Optional<OTP> result = service.getOTPByUserId(1L);

        assertTrue(result.isPresent());
        assertEquals("123456", result.get().getOtp());
    }
}

