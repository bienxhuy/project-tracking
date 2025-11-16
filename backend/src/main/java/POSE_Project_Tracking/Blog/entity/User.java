package POSE_Project_Tracking.Blog.entity;

import POSE_Project_Tracking.Blog.enums.ELoginType;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private EUserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private EUserStatus accountStatus;

    @Column(name = "level")
    private double level;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private ELoginType loginType;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private List<Project> instructedProjects;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMemberships;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private List<Task> assignedTasks;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Report> submittedReports;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL)
    private List<Attachment> uploadedAttachments;
}
