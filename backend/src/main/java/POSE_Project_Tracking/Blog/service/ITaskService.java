package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.TaskReq;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;

import java.util.List;

public interface ITaskService {
    TaskRes createTask(TaskReq taskReq);
    TaskRes updateTask(Long id, TaskReq taskReq);
    TaskRes getTaskById(Long id, String include);
    TaskRes getTaskWithDetails(Long id);
    List<TaskRes> getAllTasks();
    List<TaskRes> getTasksByProject(Long projectId);
    List<TaskRes> getTasksByMilestone(Long milestoneId, String include);
    List<TaskRes> getTasksByAssignee(Long assigneeId);
    List<TaskRes> getTasksByUser(Long userId);  // Alias for getTasksByAssignee
    List<TaskRes> getTasksByStatus(ETaskStatus status);
    List<TaskRes> getOverdueTasks();
    List<TaskRes> getOverdueTasksByUser(Long userId);
    List<TaskRes> searchTasks(String keyword);
    void deleteTask(Long id);
    void lockTask(Long id, Long userId);
    void unlockTask(Long id);
    TaskRes toggleTaskLock(Long id, Boolean isLocked);
    void assignTask(Long taskId, Long userId);
    void updateTaskStatus(Long id, ETaskStatus status);
    void markTaskAsCompleted(Long id);
    void lockTaskWithChildren(Long id);
}
