package POSE_Project_Tracking.Blog.dto;

import POSE_Project_Tracking.Blog.enums.EUserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class UserClaim {

    Long id;

    EUserRole role;

    String username;

    String email;

}
