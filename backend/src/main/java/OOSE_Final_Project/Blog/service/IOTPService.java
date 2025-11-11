package OOSE_Final_Project.Blog.service;

import OOSE_Final_Project.Blog.entity.OTP;

import java.util.Optional;

public interface IOTPService {

    OTP generateOTP(Long userId);

    Optional<OTP> getOTPById(Long id);

    Optional<OTP> getOTPByUserId(Long userId);

    boolean verifyOTP(Long userId, String otp,String email);

    void deleteOTP(Long id);

    OTP generateOTPForgotPassword(Long userId);
}
