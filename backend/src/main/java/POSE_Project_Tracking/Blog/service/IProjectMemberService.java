package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.ProjectMemberReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectMemberRes;
import POSE_Project_Tracking.Blog.enums.EUserRole;

import java.util.List;

public interface IProjectMemberService {
    ProjectMemberRes addMember(ProjectMemberReq projectMemberReq);
    ProjectMemberRes updateMemberRole(Long id, EUserRole role);
    ProjectMemberRes updateMember(Long id, ProjectMemberReq projectMemberReq);
    ProjectMemberRes getMemberById(Long id);
    List<ProjectMemberRes> getAllMembers();
    List<ProjectMemberRes> getMembersByProject(Long projectId);
    List<ProjectMemberRes> getProjectsByUser(Long userId);
    List<ProjectMemberRes> getMembersByUser(Long userId);
    List<ProjectMemberRes> getMembersByRole(EUserRole role);
    void removeMember(Long id);
    void deactivateMember(Long id);
    void activateMember(Long id);
}
