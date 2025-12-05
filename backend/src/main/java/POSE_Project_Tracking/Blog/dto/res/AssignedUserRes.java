package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EUserRole;
import lombok.*;

/**
 * Standard user information response used across all DTOs
 * Represents user info for assigned users, members, authors, etc.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedUserRes {
    
    private Long id;
    private String displayName;
    private String email;
    private String studentId;
    private EUserRole role;
}
