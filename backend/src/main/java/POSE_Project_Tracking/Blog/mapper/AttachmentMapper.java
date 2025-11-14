package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.res.AttachmentRes;
import POSE_Project_Tracking.Blog.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "milestone", source = "milestone")
    @Mapping(target = "task", source = "task")
    @Mapping(target = "report", source = "report")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "uploadedBy", source = "uploadedBy")
    Attachment toEntity(String fileName, String filePath, String fileType, Long fileSize, String url,
                       Project project, Milestone milestone, Task task, Report report, 
                       Comment comment, User uploadedBy);

    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    @Mapping(target = "uploadedByName", source = "uploadedBy.displayName")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "milestoneId", source = "milestone.id")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "commentId", source = "comment.id")
    AttachmentRes toResponse(Attachment attachment);
}
