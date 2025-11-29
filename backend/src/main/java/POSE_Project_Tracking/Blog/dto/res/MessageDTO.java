package POSE_Project_Tracking.Blog.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageDTO {
    private String from;
    private String to;
    private String toName;
    private String subject;
    private String OTP;
    private String password; // For sending temporary password to new users
}
