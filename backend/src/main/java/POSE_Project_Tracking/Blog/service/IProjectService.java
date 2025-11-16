package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;

import java.util.List;

public interface IProjectService {
    
    ProjectRes createProject(ProjectReq projectReq);
    
    ProjectRes updateProject(Long id, ProjectReq projectReq);
    
    ProjectRes getProjectById(Long id);
    
    ProjectRes getProjectWithDetails(Long id);
    
    List<ProjectRes> getAllProjects();
    
    List<ProjectRes> getProjectsByInstructor(Long instructorId);
    
    List<ProjectRes> getProjectsByStatus(EProjectStatus status);
    
    List<ProjectRes> getProjectsByYearAndSemester(Integer year, Integer semester);
    
    List<ProjectRes> searchProjects(String keyword);
    
    void deleteProject(Long id);
    
    void lockProject(Long id, Long userId);
    
    void unlockProject(Long id);
    
    void updateProjectCompletion(Long id);
}
