package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MilestoneMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "completionPercentage", constant = "0.0f")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", source = "milestoneReq.title")
    @Mapping(target = "description", source = "milestoneReq.description")
    @Mapping(target = "startDate", source = "milestoneReq.startDate")
    @Mapping(target = "endDate", source = "milestoneReq.endDate")
    @Mapping(target = "project", source = "project")
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Milestone toEntity(MilestoneReq milestoneReq, Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completionPercentage", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntityFromRequest(MilestoneReq milestoneReq, @MappingTarget Milestone milestone);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "totalReports", expression = "java(milestone.getReports() != null ? milestone.getReports().size() : 0)")
    MilestoneRes toResponse(Milestone milestone);
}
