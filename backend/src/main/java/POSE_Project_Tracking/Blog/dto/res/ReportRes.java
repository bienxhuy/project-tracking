package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EReportStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRes {

    private Long id;
    private String title;
    private String content;
    private EReportStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Project info
    private Long projectId;
    private String projectTitle;
    
    // Milestone info (optional)
    private Long milestoneId;
    private String milestoneTitle;
    
    // Task info (optional)
    private Long taskId;
    private String taskTitle;
    
    // Submitted by
    private Long submittedById;
    private String submittedByName;
    
    // Statistics
    private Integer totalComments;
    private Integer totalAttachments;
}
