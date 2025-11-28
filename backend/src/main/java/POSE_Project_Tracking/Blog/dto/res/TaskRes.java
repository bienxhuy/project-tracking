package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRes {

    private Long id;
    private String title;
    private String description;
    private ETaskStatus status;
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
    
    // Assigned user info
    private Long assignedToId;
    private String assignedToName;
    
    // Statistics
    private Integer totalReports;
    private Integer totalComments;
}
