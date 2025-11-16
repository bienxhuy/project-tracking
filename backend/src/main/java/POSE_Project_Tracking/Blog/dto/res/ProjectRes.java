package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRes {

    private Long id;
    private String title;
    private String content;
    private String objectives;
    private Integer year;
    private Integer semester;
    private String faculty;
    private String batch;
    private EProjectStatus status;
    private Float completionPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isLocked;
    private Boolean isOnlyDesLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Instructor info
    private Long instructorId;
    private String instructorName;
    
    // Statistics
    private Integer totalMilestones;
    private Integer totalTasks;
    private Integer totalMembers;
}
