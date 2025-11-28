package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    
    // Assigned users info (multiple users can work on same task)
    @JsonProperty("assignees")
    private List<AssignedUserRes> assignedUsers;

    // Reports (optional - only included when requested via query parameter)
    private List<ReportRes> reports;

    // Statistics
    private Integer totalReports;
    private Integer totalComments;
}
