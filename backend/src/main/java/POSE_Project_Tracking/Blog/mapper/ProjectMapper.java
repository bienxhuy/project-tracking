package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.AssignedUserRes;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MilestoneMapper.class})
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "completionPercentage", constant = "0.0f")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "isObjDesLocked", constant = "false")
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "instructor", source = "instructor")
    @Mapping(target = "milestones", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "members", ignore = true)
    Project toEntity(ProjectReq projectReq, User instructor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completionPercentage", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "isObjDesLocked", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "milestones", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "members", ignore = true)
    void updateEntityFromRequest(ProjectReq projectReq, @MappingTarget Project project);

    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "instructorName", source = "instructor.displayName")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "totalMilestones", expression = "java(project.getMilestones() != null ? project.getMilestones().size() : 0)")
    @Mapping(target = "totalCompletedMilestones", expression = "java(project.getMilestones() != null ? (int) project.getMilestones().stream().filter(m -> m.getStatus() == POSE_Project_Tracking.Blog.enums.EMilestoneStatus.COMPLETED).count() : 0)")
    @Mapping(target = "totalTasks", expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    @Mapping(target = "totalCompletedTasks", expression = "java(project.getTasks() != null ? (int) project.getTasks().stream().filter(t -> t.getStatus() == POSE_Project_Tracking.Blog.enums.ETaskStatus.COMPLETED).count() : 0)")
    @Mapping(target = "totalMembers", expression = "java(project.getMembers() != null ? project.getMembers().size() : 0)")
    @Mapping(target = "milestones", source = "milestones", qualifiedByName = "toResponse")
    @Mapping(target = "students", source = "members", qualifiedByName = "toAssignedUserRes")
    ProjectRes toResponse(Project project);
    
    // Specialized mapping for detailed project view with nested tasks in milestones
    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "instructorName", source = "instructor.displayName")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "totalMilestones", expression = "java(project.getMilestones() != null ? project.getMilestones().size() : 0)")
    @Mapping(target = "totalCompletedMilestones", expression = "java(project.getMilestones() != null ? (int) project.getMilestones().stream().filter(m -> m.getStatus() == POSE_Project_Tracking.Blog.enums.EMilestoneStatus.COMPLETED).count() : 0)")
    @Mapping(target = "totalTasks", expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    @Mapping(target = "totalCompletedTasks", expression = "java(project.getTasks() != null ? (int) project.getTasks().stream().filter(t -> t.getStatus() == POSE_Project_Tracking.Blog.enums.ETaskStatus.COMPLETED).count() : 0)")
    @Mapping(target = "totalMembers", expression = "java(project.getMembers() != null ? project.getMembers().size() : 0)")
    @Mapping(target = "milestones", source = "milestones", qualifiedByName = "toResponseWithTasks")
    @Mapping(target = "students", source = "members", qualifiedByName = "toAssignedUserRes")
    ProjectRes toResponseWithDetails(Project project);
    
    // Map ProjectMember to AssignedUserRes
    @Named("toAssignedUserRes")
    default AssignedUserRes toAssignedUserRes(ProjectMember member) {
        if (member == null || member.getUser() == null) {
            return null;
        }
        return AssignedUserRes.builder()
                .id(member.getUser().getId())
                .displayName(member.getUser().getDisplayName())
                .email(member.getUser().getEmail())
                .studentId(member.getUser().getStudentId())
                .role(member.getUser().getRole() != null ? member.getUser().getRole() : null)
                .build();
    }
}
