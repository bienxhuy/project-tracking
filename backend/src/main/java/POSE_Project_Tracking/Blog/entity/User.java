package POSE_Project_Tracking.Blog.entity;

import POSE_Project_Tracking.Blog.enums.ELoginType;
import POSE_Project_Tracking.Blog.enums.ERole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    private String username;

    private String password;

    private String email;

    private String displayName;

    private String avatar;

    private ERole role;

    private EUserStatus accountStatus;

    private double level;

    private ELoginType loginType;


}
