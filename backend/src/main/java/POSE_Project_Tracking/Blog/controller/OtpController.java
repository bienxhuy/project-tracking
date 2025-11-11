package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.OTPReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.facade.UserOTPFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/otp")
public class OtpController {


    @Autowired
    UserOTPFacade userOTPFacade;

    @GetMapping("")
    public ApiResponse<Boolean> generateNewOTP(@RequestParam("userId") String userId) {
        userOTPFacade.generateOTP(Long.valueOf(userId));
        return new ApiResponse<>(HttpStatus.CREATED, "Generate new OTP", Boolean.TRUE, null);
    }

    @PostMapping("")
    public ApiResponse<Boolean> verifyOTP(@RequestBody OTPReq otpReq) {
        var result = userOTPFacade.verifyOTP(otpReq.getUserId(), otpReq.getOtp(), otpReq.getEmail());
        return new ApiResponse<>(HttpStatus.OK, "Verify OTP", result, null);
    }

    @GetMapping("/forgot-password")
    public ApiResponse<Boolean> generateNewOTPForgotPassword(@RequestParam("email") String email) {
        userOTPFacade.generateOTPForPassword(email);
        return new ApiResponse<>(HttpStatus.CREATED, "Generate new OTP for forgot password", Boolean.TRUE, null);
    }

    @GetMapping("/email/{email}")
    public ApiResponse<Boolean> generateNewOTPWithEmail(@PathVariable String email) {
        userOTPFacade.generateOTPWithEmail(email);
        return new ApiResponse<>(HttpStatus.CREATED, "Generate new OTP", Boolean.TRUE, null);
    }

}
