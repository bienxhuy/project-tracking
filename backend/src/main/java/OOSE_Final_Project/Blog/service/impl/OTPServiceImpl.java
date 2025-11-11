package OOSE_Final_Project.Blog.service.impl;

import OOSE_Final_Project.Blog.entity.OTP;
import OOSE_Final_Project.Blog.entity.User;
import OOSE_Final_Project.Blog.enums.EUserStatus;
import OOSE_Final_Project.Blog.repository.OTPRepository;
import OOSE_Final_Project.Blog.repository.UserRepository;
import OOSE_Final_Project.Blog.service.IOTPService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
public class OTPServiceImpl implements IOTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int OTP_LENGTH = 6;

    private static final String OTP_CHARS = "0123456789";

    @Override
    public OTP generateOTP(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        if (user.getAccountStatus() != EUserStatus.VERIFYING) {
            throw new IllegalArgumentException("User is not in verifying status");
        }

        // Xóa OTP cũ nếu tồn tại
        otpRepository.findByUserId(userId)
                     .ifPresent(otpRepository::delete);

        // Tạo OTP mới
        String otp = generateRandomOTP();
        OTP newOTP = new OTP();
        newOTP.setUser(user);
        newOTP.setOtp(otp);

        return otpRepository.save(newOTP);
    }

    @Override
    public OTP generateOTPForgotPassword(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        // Xóa OTP cũ nếu tồn tại
        otpRepository.findByUserId(userId)
                     .ifPresent(otpRepository::delete);

        // Tạo OTP mới
        String otp = generateRandomOTP();
        OTP newOTP = new OTP();
        newOTP.setUser(user);
        newOTP.setOtp(otp);

        return otpRepository.save(newOTP);
    }

    @Override
    public Optional<OTP> getOTPById(Long id) {
        return otpRepository.findById(id);
    }

    @Override
    public Optional<OTP> getOTPByUserId(Long userId) {
        return otpRepository.findByUserId(userId);
    }

    @Override
    public boolean verifyOTP(Long userId, String otp, String email) {
        Optional<OTP> otpEntity = null;
        if (userId != null) {
            otpEntity = otpRepository.findByUserId(userId);
        } else {
            var user = userRepository.findByEmail(email)
                                     .orElseThrow(
                                             () -> new IllegalArgumentException("User not found with email: " + email));
            otpEntity = otpRepository.findByUserId(user.getId());
        }
        if (otpEntity.isPresent() && otpEntity.get()
                                              .getOtp()
                                              .equals(otp)) {
            // Xóa OTP sau khi xác minh thành công
            otpRepository.deleteById(otpEntity.get()
                                              .getId());
            return true;
        }
        return false;
    }

    @Override
    public void deleteOTP(Long id) {
        if (!otpRepository.existsById(id)) {
            throw new IllegalArgumentException("OTP not found with id: " + id);
        }
        otpRepository.deleteById(id);
    }

    private String generateRandomOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
        }
        return otp.toString();
    }
}
