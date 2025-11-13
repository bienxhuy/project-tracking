package POSE_Project_Tracking.Blog.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPReq {

    private String otp;

    private Long userId;

    private String email;
}
