package POSE_Project_Tracking.Blog.dto;

import POSE_Project_Tracking.Blog.enums.ERole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class UserClaim {

    Long id;

    ERole role;

    String username;

    String email;

}
