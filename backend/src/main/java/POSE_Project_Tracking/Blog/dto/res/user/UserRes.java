package POSE_Project_Tracking.Blog.dto.res.user;

import POSE_Project_Tracking.Blog.enums.EUserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRes {

    Long id;

    private String username;

    private String email;

    private String displayName;

    private String studentId;

    private EUserRole role;

}
