package POSE_Project_Tracking.Blog.dto.req;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContentReq {
    
    private String objective;
    
    private String content;
}
