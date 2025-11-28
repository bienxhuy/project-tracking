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

    // Student projects with filters
    List<ProjectRes> getProjectsByStudent(Long studentId, Integer year, Integer semester, String batch);

    List<ProjectRes> getProjectsByStudentAndStatus(Long studentId, EProjectStatus status, Integer year, Integer semester, String batch);

    List<ProjectRes> getMyProjects(Integer year, Integer semester, String batch);

    List<ProjectRes> getMyProjectsByStatus(EProjectStatus status, Integer year, Integer semester, String batch);

    List<ProjectRes> getAllProjectsWithFilters(Integer year, Integer semester, String batch);

    // Student/Instructor projects without filters (get all)
    List<ProjectRes> getAllProjectsByStudent(Long studentId);

    List<ProjectRes> getAllProjectsByStudentAndStatus(Long studentId, EProjectStatus status);

    List<ProjectRes> getAllMyProjects();

    List<ProjectRes> getAllMyProjectsByStatus(EProjectStatus status);
}
