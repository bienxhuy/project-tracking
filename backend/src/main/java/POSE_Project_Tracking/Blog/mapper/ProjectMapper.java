package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.dto.res.ProjectMemberRes;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MilestoneMapper.class, ProjectMemberMapper.class})
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "completionPercentage", constant = "0.0f")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "isOnlyDesLocked", constant = "false")
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
    @Mapping(target = "isOnlyDesLocked", ignore = true)
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
    @Mapping(target = "totalTasks", expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    @Mapping(target = "totalMembers", expression = "java(project.getMembers() != null ? project.getMembers().size() : 0)")
    @Mapping(target = "milestones", source = "milestones", qualifiedByName = "toResponse")
    @Mapping(target = "students", source = "members")
    ProjectRes toResponse(Project project);
}
