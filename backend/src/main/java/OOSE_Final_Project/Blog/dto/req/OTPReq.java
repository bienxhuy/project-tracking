package OOSE_Final_Project.Blog.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPReq {

    private String otp;

    private Long userId;

    private String email;
}
