package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EUserRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberRes {

    private Long id;
    private EUserRole role;
    private Boolean isActive;
    private LocalDateTime joinedAt;
    
    // Project info
    private Long projectId;
    private String projectTitle;
    
    // User info
    private Long userId;
    private String userName;
    private String userEmail;
    private String userAvatar;
}
