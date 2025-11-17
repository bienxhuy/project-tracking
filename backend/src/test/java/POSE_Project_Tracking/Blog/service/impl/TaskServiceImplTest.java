package POSE_Project_Tracking.Blog.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import POSE_Project_Tracking.Blog.dto.req.TaskReq;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.TaskMapper;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private TaskServiceImpl service;

    private Task task;
    private Project project;
    private User user;
    private User currentUser;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);

        user = new User();
        user.setId(1L);

        currentUser = new User();
        currentUser.setId(2L);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(ETaskStatus.IN_PROGRESS);
        task.setLocked(false);
        task.setProject(project);
    }

    @Test
    void createTask_validRequest_createsTask() {
        TaskReq req = new TaskReq();
        req.setProjectId(1L);
        req.setAssigneeId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskMapper.toEntity(req, project, user)).thenReturn(task);
        when(securityUtil.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(new TaskRes());

        TaskRes result = service.createTask(req);

        assertNotNull(result);
        verify(taskRepository).save(task);
        assertEquals(currentUser, task.getCreatedBy());
    }

    @Test
    void createTask_projectNotFound_throwsException() {
        TaskReq req = new TaskReq();
        req.setProjectId(999L);

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.createTask(req));
    }

    @Test
    void updateTask_validRequest_updatesTask() {
        TaskReq req = new TaskReq();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(new TaskRes());

        TaskRes result = service.updateTask(1L, req);

        assertNotNull(result);
        verify(taskMapper).updateEntityFromRequest(req, task);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_lockedTask_throwsException() {
        task.setLocked(true);
        TaskReq req = new TaskReq();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(CustomException.class, () -> service.updateTask(1L, req));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_unlockedTask_deletesTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        service.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_lockedTask_throwsException() {
        task.setLocked(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(CustomException.class, () -> service.deleteTask(1L));
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void assignTask_validRequest_assignsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        service.assignTask(1L, 1L);

        assertEquals(user, task.getAssignedTo());
        verify(taskRepository).save(task);
    }

    @Test
    void assignTask_lockedTask_throwsException() {
        task.setLocked(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(CustomException.class, () -> service.assignTask(1L, 1L));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskStatus_validRequest_updatesStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        service.updateTaskStatus(1L, ETaskStatus.IN_PROGRESS);

        assertEquals(ETaskStatus.IN_PROGRESS, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void markTaskAsCompleted_validRequest_marksCompleted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        service.markTaskAsCompleted(1L);

        assertEquals(ETaskStatus.COMPLETED, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void lockTask_validRequest_locksTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        service.lockTask(1L, 1L);

        assertTrue(task.getLocked());
        assertEquals(user, task.getLockedBy());
        assertNotNull(task.getLockedAt());
        verify(taskRepository).save(task);
    }

    @Test
    void unlockTask_lockedTask_unlocksTask() {
        task.setLocked(true);
        task.setLockedBy(user);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        service.unlockTask(1L);

        assertEquals(false, task.getLocked());
        assertEquals(null, task.getLockedBy());
        assertEquals(null, task.getLockedAt());
        verify(taskRepository).save(task);
    }

    @Test
    void getTaskById_existingTask_returnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(new TaskRes());

        TaskRes result = service.getTaskById(1L);

        assertNotNull(result);
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_nonExistingTask_throwsException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getTaskById(999L));
    }
}

