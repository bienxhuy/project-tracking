package OOSE_Final_Project.Blog.dto.res;

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
}
