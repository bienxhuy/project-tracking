package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.entity.OTP;

import java.util.Optional;

public interface IOTPService {

    OTP generateOTP(Long userId);

    Optional<OTP> getOTPById(Long id);

    Optional<OTP> getOTPByUserId(Long userId);

    boolean verifyOTP(Long userId, String otp,String email);

    void deleteOTP(Long id);

    OTP generateOTPForgotPassword(Long userId);
}
