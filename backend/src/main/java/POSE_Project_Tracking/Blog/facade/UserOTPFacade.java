package POSE_Project_Tracking.Blog.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.res.MessageDTO;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.OTP;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.service.IEmailService;
import POSE_Project_Tracking.Blog.service.IOTPService;
import POSE_Project_Tracking.Blog.service.IUserService;

@Service
public class UserOTPFacade {

    @Autowired
    IUserService userService;

    @Autowired
    IOTPService otpService;

    @Autowired
    IEmailService emailService;

    public UserRes createUser(UserReq req) {
        // Save the original password before it gets hashed
        String originalPassword = req.getPassword();
        
        UserRes res = userService.createUser(req);
        
        // Send account info email with temporary password
        sendAccountInfoToEmail(res, originalPassword);
        
        return res;
    }
    
    void sendAccountInfoToEmail(UserRes user, String temporaryPassword) {
        try {
            MessageDTO messageDTO = MessageDTO.builder()
                                              .from("Nguyentruongpro19@gmail.com")
                                              .to(user.getEmail())
                                              .subject("Tài khoản UTEPs của bạn đã được tạo")
                                              .toName(user.getDisplayName())
                                              .password(temporaryPassword)
                                              .build();

            emailService.sendEmail(messageDTO);
        }
        catch (Exception e) {
            throw new RuntimeException("Không thể gửi email thông tin tài khoản, vui lòng thử lại");
        }
    }

    public void generateOTP(Long userId) {
        OTP otp = otpService.generateOTP(Long.valueOf(userId));
        var user = userService.getUserById(userId);
        sendOTPToEmail(user, otp);
    }

    public void generateOTPWithEmail(String email) {
        var user = userService.getUserByEmail(email);
        OTP otp = otpService.generateOTP(user.getId());
        sendOTPToEmail(user, otp);
    }

    void sendOTPToEmail(UserRes user, OTP otp) {
        try {
            MessageDTO messageDTO = MessageDTO.builder()
                                              .from("Nguyentruongpro19@gmail.com")
                                              .to(user.getEmail())
                                              .subject("Xác nhận mã OTP cho tài khoản của bạn")
                                              .toName(user.getUsername())
                                              .OTP(otp.getOtp())
                                              .build();

            emailService.sendEmail(messageDTO);
        }
        catch (Exception e) {
            // Rollback user and OTP nếu cần
            throw new RuntimeException("Không thể gửi email xác thực OTP, vui lòng thử lại");
        }
    }

    public void generateOTPForPassword(String email) {
        var user = userService.getUserByEmail(email);
        var userId = user.getId();
        OTP otp = otpService.generateOTPForgotPassword(userId);
        sendOTPToEmail(user, otp);

    }

    public boolean verifyOTP(Long userId, String otp, String email) {
        var result = otpService.verifyOTP(userId, otp, email);
        if (!result) {
            throw new IllegalArgumentException("OTP verification failed");
        }
        if (userId == null) {
            var user=userService.getUserByEmail(email);
            userService.updateUserStatus(user.getId(), EUserStatus.ACTIVE);
            return true;
        }
        userService.updateUserStatus(userId, EUserStatus.ACTIVE);
        return true;

    }

    /**
     * Send batch account info emails asynchronously for bulk user creation
     * @param users List of created users
     * @param passwords List of original passwords corresponding to users
     */
    public void sendBatchAccountInfoEmails(List<UserRes> users, List<String> passwords) {
        if (users.size() != passwords.size()) {
            throw new IllegalArgumentException("Users and passwords lists must have the same size");
        }
        
        for (int i = 0; i < users.size(); i++) {
            try {
                sendAccountInfoToEmail(users.get(i), passwords.get(i));
            } catch (Exception e) {
                // Log error but continue with other emails
                System.err.println("Failed to send email to " + users.get(i).getEmail() + ": " + e.getMessage());
            }
        }
    }
}
