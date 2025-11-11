package POSE_Project_Tracking.Blog.facade;

import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.res.MessageDTO;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.OTP;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.service.IEmailService;
import POSE_Project_Tracking.Blog.service.IOTPService;
import POSE_Project_Tracking.Blog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOTPFacade {

    @Autowired
    IUserService userService;

    @Autowired
    IOTPService otpService;

    @Autowired
    IEmailService emailService;

    public UserRes createUser(UserReq req) {
        UserRes res = userService.createUser(req);
        OTP otp = otpService.generateOTP(res.getId());
        sendOTPToEmail(res, otp);
        return res;
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
}
