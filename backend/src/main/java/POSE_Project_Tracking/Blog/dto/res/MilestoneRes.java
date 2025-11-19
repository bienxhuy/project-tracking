package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneRes {

    private Long id;
    private String title;
    private String description;
    private EMilestoneStatus status;
    private Integer orderNumber;
    private Float completionPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Project info
    private Long projectId;
    private String projectTitle;
    
    // Statistics
    private Integer totalReports;
}
