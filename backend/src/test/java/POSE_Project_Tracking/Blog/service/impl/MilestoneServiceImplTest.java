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

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.MilestoneMapper;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class MilestoneServiceImplTest {

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private MilestoneServiceImpl service;

    private Milestone milestone;
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

        milestone = new Milestone();
        milestone.setId(1L);
        milestone.setTitle("Test Milestone");
        milestone.setStatus(EMilestoneStatus.IN_PROGRESS);
        milestone.setLocked(false);
        milestone.setProject(project);
    }

    @Test
    void createMilestone_validRequest_createsMilestone() {
        MilestoneReq req = new MilestoneReq();
        req.setProjectId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(milestoneMapper.toEntity(req, project)).thenReturn(milestone);
        when(securityUtil.getCurrentUser()).thenReturn(currentUser);
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);
        when(milestoneMapper.toResponse(milestone)).thenReturn(new MilestoneRes());

        MilestoneRes result = service.createMilestone(req);

        assertNotNull(result);
        verify(milestoneRepository).save(milestone);
        assertEquals(currentUser, milestone.getCreatedBy());
    }

    @Test
    void createMilestone_projectNotFound_throwsException() {
        MilestoneReq req = new MilestoneReq();
        req.setProjectId(999L);

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.createMilestone(req));
    }

    @Test
    void updateMilestone_validRequest_updatesMilestone() {
        MilestoneReq req = new MilestoneReq();

        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);
        when(milestoneMapper.toResponse(milestone)).thenReturn(new MilestoneRes());

        MilestoneRes result = service.updateMilestone(1L, req);

        assertNotNull(result);
        verify(milestoneMapper).updateEntityFromRequest(req, milestone);
        verify(milestoneRepository).save(milestone);
    }

    @Test
    void updateMilestone_lockedMilestone_throwsException() {
        milestone.setLocked(true);
        MilestoneReq req = new MilestoneReq();

        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));

        assertThrows(CustomException.class, () -> service.updateMilestone(1L, req));
        verify(milestoneRepository, never()).save(any());
    }

    @Test
    void deleteMilestone_unlockedMilestone_deletesMilestone() {
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));

        service.deleteMilestone(1L);

        verify(milestoneRepository).delete(milestone);
    }

    @Test
    void deleteMilestone_lockedMilestone_throwsException() {
        milestone.setLocked(true);

        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));

        assertThrows(CustomException.class, () -> service.deleteMilestone(1L));
        verify(milestoneRepository, never()).delete(any());
    }

    @Test
    void lockMilestone_validRequest_locksMilestone() {
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

        service.lockMilestone(1L, 1L);

        assertTrue(milestone.getLocked());
        assertEquals(user, milestone.getLockedBy());
        assertNotNull(milestone.getLockedAt());
        verify(milestoneRepository).save(milestone);
    }

    @Test
    void unlockMilestone_lockedMilestone_unlocksMilestone() {
        milestone.setLocked(true);
        milestone.setLockedBy(user);

        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

        service.unlockMilestone(1L);

        assertEquals(false, milestone.getLocked());
        assertEquals(null, milestone.getLockedBy());
        assertEquals(null, milestone.getLockedAt());
        verify(milestoneRepository).save(milestone);
    }

    @Test
    void updateMilestoneCompletion_noTasks_setsZeroPercentage() {
        milestone.setTasks(new java.util.ArrayList<>());

        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

        service.updateMilestoneCompletion(1L);

        assertEquals(0.0f, milestone.getCompletionPercentage());
        verify(milestoneRepository).save(milestone);
    }

    @Test
    void updateMilestoneStatus_validRequest_updatesStatus() {
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(milestone);

        service.updateMilestoneStatus(1L, EMilestoneStatus.IN_PROGRESS);

        assertEquals(EMilestoneStatus.IN_PROGRESS, milestone.getStatus());
        verify(milestoneRepository).save(milestone);
    }

    @Test
    void updateMilestoneStatus_lockedMilestone_throwsException() {
        milestone.setLocked(true);

        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));

        assertThrows(CustomException.class, () -> service.updateMilestoneStatus(1L, EMilestoneStatus.IN_PROGRESS));
        verify(milestoneRepository, never()).save(any());
    }

    @Test
    void getMilestoneById_existingMilestone_returnsMilestone() {
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(milestone));
        when(milestoneMapper.toResponse(milestone)).thenReturn(new MilestoneRes());

        MilestoneRes result = service.getMilestoneById(1L);

        assertNotNull(result);
        verify(milestoneRepository).findById(1L);
    }

    @Test
    void getMilestoneById_nonExistingMilestone_throwsException() {
        when(milestoneRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getMilestoneById(999L));
    }
}

