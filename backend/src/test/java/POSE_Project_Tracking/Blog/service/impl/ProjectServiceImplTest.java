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

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ProjectMapper;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ProjectServiceImpl service;

    private Project project;
    private User instructor;
    private User currentUser;

    @BeforeEach
    void setUp() {
        instructor = new User();
        instructor.setId(1L);
        instructor.setUsername("instructor");

        currentUser = new User();
        currentUser.setId(2L);
        currentUser.setUsername("current");

        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setStatus(EProjectStatus.ACTIVE);
        project.setLocked(false);
    }

    @Test
    void createProject_validRequest_createsProject() {
        ProjectReq req = new ProjectReq();

        when(securityUtil.getCurrentUser()).thenReturn(currentUser);
        when(projectMapper.toEntity(req, currentUser)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(new ProjectRes());

        ProjectRes result = service.createProject(req);

        assertNotNull(result);
        verify(projectRepository).save(project);
        assertEquals(currentUser, project.getCreatedBy());
        assertEquals(currentUser, project.getInstructor());
    }

    @Test
    void updateProject_validRequest_updatesProject() {
        ProjectReq req = new ProjectReq();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(new ProjectRes());

        ProjectRes result = service.updateProject(1L, req);

        assertNotNull(result);
        verify(projectMapper).updateEntityFromRequest(req, project);
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_lockedProject_throwsException() {
        project.setLocked(true);
        ProjectReq req = new ProjectReq();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(CustomException.class, () -> service.updateProject(1L, req));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_unlockedProject_deletesProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        service.deleteProject(1L);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_lockedProject_throwsException() {
        project.setLocked(true);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(CustomException.class, () -> service.deleteProject(1L));
        verify(projectRepository, never()).delete(any());
    }

    @Test
    void lockProject_validRequest_locksProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(securityUtil.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        service.lockProject(1L);

        assertTrue(project.getLocked());
        assertEquals(currentUser, project.getLockedBy());
        assertNotNull(project.getLockedAt());
        verify(projectRepository).save(project);
    }

    @Test
    void unlockProject_lockedProject_unlocksProject() {
        project.setLocked(true);
        project.setLockedBy(currentUser);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        service.unlockProject(1L);

        assertEquals(false, project.getLocked());
        assertEquals(null, project.getLockedBy());
        assertEquals(null, project.getLockedAt());
        verify(projectRepository).save(project);
    }

    @Test
    void updateProjectCompletion_noTasks_setsZeroPercentage() {
        project.setTasks(new java.util.ArrayList<>());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        service.updateProjectCompletion(1L);

        assertEquals(0.0f, project.getCompletionPercentage());
        verify(projectRepository).save(project);
    }

    @Test
    void getProjectById_existingProject_returnsProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(new ProjectRes());

        ProjectRes result = service.getProjectById(1L);

        assertNotNull(result);
        verify(projectRepository).findById(1L);
    }

    @Test
    void getProjectById_nonExistingProject_throwsException() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getProjectById(999L));
    }
}

