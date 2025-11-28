package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.TaskReq;
import POSE_Project_Tracking.Blog.dto.res.AssignedUserRes;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReportMapper.class})
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "title", source = "taskReq.title")
    @Mapping(target = "description", source = "taskReq.description")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "milestone", ignore = true)
    @Mapping(target = "assignedUsers", source = "assignedUsers")
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Task toEntity(TaskReq taskReq, Project project, List<User> assignedUsers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "milestone", ignore = true)
    @Mapping(target = "assignedUsers", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntityFromRequest(TaskReq taskReq, @MappingTarget Task task);

    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "assignedUsers", source = "assignedUsers")
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "totalReports", expression = "java(task.getReports() != null ? task.getReports().size() : 0)")
    @Mapping(target = "totalComments", expression = "java(task.getComments() != null ? task.getComments().size() : 0)")
    TaskRes toResponse(Task task);
    
    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "assignedUsers", source = "assignedUsers")
    @Mapping(target = "reports", source = "task.reports", qualifiedByName = "toResponseSimple")
    @Mapping(target = "totalReports", expression = "java(task.getReports() != null ? task.getReports().size() : 0)")
    @Mapping(target = "totalComments", expression = "java(task.getComments() != null ? task.getComments().size() : 0)")
    TaskRes toResponseWithReports(Task task);
    
    // Map User to AssignedUserRes
    default AssignedUserRes toAssignedUserRes(User user) {
        if (user == null) {
            return null;
        }
        return AssignedUserRes.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole() : null)
                .build();
    }
}
