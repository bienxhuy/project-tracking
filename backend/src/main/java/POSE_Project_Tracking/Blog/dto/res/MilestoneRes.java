package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    
    // Lock information
    private Long lockedById;
    private String lockedByName;
    private LocalDateTime lockedAt;
    
    // Creator information
    private Long createdById;
    private String createdByName;
    
    // Project info
    private Long projectId;
    private String projectTitle;
    
    // Statistics
    private Integer tasksTotal;
    private Integer tasksCompleted;
    
    // Optional - only included when requested via query parameter
    private List<TaskRes> tasks;
}
