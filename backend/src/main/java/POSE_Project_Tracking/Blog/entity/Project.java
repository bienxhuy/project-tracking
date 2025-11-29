package POSE_Project_Tracking.Blog.entity;

import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited // Enable audit tracking for Project entity
public class Project extends ProgressEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "objectives", columnDefinition = "TEXT")
    private String objectives;

    @Column(name = "year")
    private Integer year;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "batch")
    private String batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EProjectStatus status;

    @Column(name = "completion_percentage")
    private Float completionPercentage;

    @Column(name = "is_obj_des_locked")
    private Boolean isObjDesLocked;

    // Collections are not audited by default to avoid complexity
    // Use @Audited on specific collections if needed
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<Milestone> milestones;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<Task> tasks;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<Report> reports;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<Comment> comments;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<ProjectMember> members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User instructor;
}
