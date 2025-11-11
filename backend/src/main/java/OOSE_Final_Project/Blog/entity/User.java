package OOSE_Final_Project.Blog.entity;

import OOSE_Final_Project.Blog.enums.ELoginType;
import OOSE_Final_Project.Blog.enums.ERole;
import OOSE_Final_Project.Blog.enums.EUserStatus;
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
