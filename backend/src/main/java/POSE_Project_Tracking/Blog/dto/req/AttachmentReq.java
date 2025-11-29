package POSE_Project_Tracking.Blog.dto.req;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentReq {
    
    private Long projectId;
    
    private Long milestoneId;
    
    private Long taskId;
    
    private Long reportId;
    
    private Long commentId;
}
