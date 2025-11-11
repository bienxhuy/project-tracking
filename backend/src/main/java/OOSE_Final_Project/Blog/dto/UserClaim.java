package OOSE_Final_Project.Blog.dto;

import OOSE_Final_Project.Blog.enums.ERole;
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
