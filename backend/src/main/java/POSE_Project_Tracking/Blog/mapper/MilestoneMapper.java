package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.AssignedUserRes;
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

    @Named("toResponse")
    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "tasksTotal", expression = "java(milestone.getTasks() != null ? milestone.getTasks().size() : 0)")
    @Mapping(target = "tasksCompleted", expression = "java(milestone.getTasks() != null ? (int) milestone.getTasks().stream().filter(t -> t.getStatus() == POSE_Project_Tracking.Blog.enums.ETaskStatus.COMPLETED).count() : 0)")
    @Mapping(target = "tasks", ignore = true)
    MilestoneRes toResponse(Milestone milestone);
    
    @AfterMapping
    default void mapTasksAfterMapping(Milestone source, @MappingTarget MilestoneRes target) {
        if (source.getTasks() != null && !source.getTasks().isEmpty()) {
            target.setTasks(source.getTasks().stream()
                .map(this::mapTask)
                .collect(java.util.stream.Collectors.toList()));
        }
    }
    
    // Helper method for mapping list of milestones with tasks
    default MilestoneRes toResponseWithTasks(Milestone milestone) {
        if (milestone == null) {
            return null;
        }
        
        MilestoneRes response = toResponse(milestone);
        
        // Manually set tasks if they are loaded
        if (milestone.getTasks() != null) {
            response.setTasks(milestone.getTasks().stream()
                .map(this::mapTask)
                .collect(java.util.stream.Collectors.toList()));
        }
        
        return response;
    }
    
    // Helper method to map Task to TaskRes
    @Mapping(target = "isLocked", source = "locked")
    @Mapping(target = "projectId", source = "milestone.project.id")
    @Mapping(target = "projectTitle", source = "milestone.project.title")
    @Mapping(target = "assignedUsers", source = "assignedUsers")
    @Mapping(target = "lockedById", source = "lockedBy.id")
    @Mapping(target = "lockedByName", source = "lockedBy.displayName")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.displayName")
    @Mapping(target = "totalReports", expression = "java(task.getReports() != null ? task.getReports().size() : 0)")
    @Mapping(target = "totalComments", expression = "java(task.getComments() != null ? task.getComments().size() : 0)")
    POSE_Project_Tracking.Blog.dto.res.TaskRes mapTask(POSE_Project_Tracking.Blog.entity.Task task);
    
    // Map User to AssignedUserRes
    @Mapping(target = "id", source = "id")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "email", source = "email")
    AssignedUserRes mapUserToAssignedUserRes(POSE_Project_Tracking.Blog.entity.User user);
}
