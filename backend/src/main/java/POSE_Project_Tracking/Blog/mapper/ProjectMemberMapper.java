package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.ProjectMemberReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectMemberRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.User;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface ProjectMemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "joinedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "role", source = "memberReq.role")
    ProjectMember toEntity(ProjectMemberReq memberReq, Project project, User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ProjectMemberReq memberReq, @MappingTarget ProjectMember member);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.displayName")
    @Mapping(target = "userEmail", source = "user.email")
    ProjectMemberRes toResponse(ProjectMember member);
}
