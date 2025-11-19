package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.ReportReq;
import POSE_Project_Tracking.Blog.dto.res.ReportRes;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SUBMITTED")
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", source = "reportReq.title")
    @Mapping(target = "content", source = "reportReq.content")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "milestone", source = "milestone")
    @Mapping(target = "task", source = "task")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Report toEntity(ReportReq reportReq, Project project, Milestone milestone, Task task, User author);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "milestone", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntityFromRequest(ReportReq reportReq, @MappingTarget Report report);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "milestoneId", source = "milestone.id")
    @Mapping(target = "milestoneTitle", source = "milestone.title")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "taskTitle", source = "task.title")
    @Mapping(target = "submittedById", source = "author.id")
    @Mapping(target = "submittedByName", source = "author.displayName")
    @Mapping(target = "totalComments", expression = "java(report.getComments() != null ? report.getComments().size() : 0)")
    @Mapping(target = "totalAttachments", expression = "java(report.getAttachments() != null ? report.getAttachments().size() : 0)")
    ReportRes toResponse(Report report);
}
