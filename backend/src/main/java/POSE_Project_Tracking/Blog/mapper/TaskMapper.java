package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.TaskReq;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", source = "taskReq.title")
    @Mapping(target = "description", source = "taskReq.description")
    @Mapping(target = "startDate", source = "taskReq.startDate")
    @Mapping(target = "endDate", source = "taskReq.endDate")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "assignedTo", source = "assignedTo")
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Task toEntity(TaskReq taskReq, Project project, User assignedTo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntityFromRequest(TaskReq taskReq, @MappingTarget Task task);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", source = "assignedTo.displayName")
    @Mapping(target = "totalReports", expression = "java(task.getReports() != null ? task.getReports().size() : 0)")
    @Mapping(target = "totalComments", expression = "java(task.getComments() != null ? task.getComments().size() : 0)")
    TaskRes toResponse(Task task);
}
