package POSE_Project_Tracking.Blog.dto.res.user;

import POSE_Project_Tracking.Blog.enums.ELoginType;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRes {

    Long id;

    private String username;

    private String email;

    private String displayName;

    EUserStatus accountStatus;

    private ELoginType loginType;

    private EUserRole role;

}
