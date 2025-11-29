package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.CommentReq;
import POSE_Project_Tracking.Blog.dto.res.CommentRes;
import POSE_Project_Tracking.Blog.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "content", source = "commentReq.content")
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "milestone", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "report", source = "report")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "parentComment", source = "parentComment")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Comment toEntity(CommentReq commentReq, Report report, User author, Comment parentComment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "milestone", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "report", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntityFromRequest(CommentReq commentReq, @MappingTarget Comment comment);

    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.displayName")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "milestoneId", source = "milestone.id")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "totalReplies", expression = "java(comment.getReplies() != null ? comment.getReplies().size() : 0)")
    CommentRes toResponse(Comment comment);
}
