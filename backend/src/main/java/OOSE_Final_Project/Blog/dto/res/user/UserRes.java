package OOSE_Final_Project.Blog.dto.res.user;

import OOSE_Final_Project.Blog.enums.ELoginType;
import OOSE_Final_Project.Blog.enums.ERole;
import OOSE_Final_Project.Blog.enums.EUserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRes {

    Long id;

    private String username;

    private String email;

    private String displayName;

    private String avatar;

    private double level;

    EUserStatus accountStatus;

    private ELoginType loginType;

    private ERole role;

}
