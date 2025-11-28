package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.AuditRevisionDTO;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Envers audit functionality
 * 
 * Tests verify that:
 * - All changes are tracked automatically
 * - Revision history can be queried
 * - Username and timestamp are captured
 * - Old states can be retrieved
 */
@SpringBootTest
@Transactional
class AuditServiceIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AuditService auditService;

    @Test
    @WithMockUser(username = "john@example.com")
    void testProjectAuditTracking() {
        // Step 1: Create a new project
        Project project = Project.builder()
            .title("Test Project for Audit")
            .content("Initial content")
            .status(EProjectStatus.ACTIVE)
            .year(2024)
            .semester(1)
            .build();
        
        project = projectRepository.save(project);
        Long projectId = project.getId();
        
        // Step 2: Update project status to LOCKED
        project.setStatus(EProjectStatus.LOCKED);
        projectRepository.save(project);
        
        // Step 3: Update again to COMPLETED
        project.setStatus(EProjectStatus.COMPLETED);
        projectRepository.save(project);
        
        // Step 4: Verify audit history
        List<AuditRevisionDTO> history = auditService.getProjectHistory(projectId);
        
        // Should have 3 revisions: INSERT, UPDATE (LOCKED), UPDATE (COMPLETED)
        assertEquals(3, history.size(), "Should have 3 revisions");
        
        // Verify first revision (INSERT)
        AuditRevisionDTO rev1 = history.get(0);
        assertEquals("INSERT", rev1.getRevisionType());
        assertEquals("john@example.com", rev1.getUsername());
        assertNotNull(rev1.getTimestamp());
        
        Project projectRev1 = (Project) rev1.getEntityData();
        assertEquals(EProjectStatus.ACTIVE, projectRev1.getStatus());
        
        // Verify second revision (UPDATE to LOCKED)
        AuditRevisionDTO rev2 = history.get(1);
        assertEquals("UPDATE", rev2.getRevisionType());
        
        Project projectRev2 = (Project) rev2.getEntityData();
        assertEquals(EProjectStatus.LOCKED, projectRev2.getStatus());
        
        // Verify third revision (UPDATE to COMPLETED)
        AuditRevisionDTO rev3 = history.get(2);
        assertEquals("UPDATE", rev3.getRevisionType());
        
        Project projectRev3 = (Project) rev3.getEntityData();
        assertEquals(EProjectStatus.COMPLETED, projectRev3.getStatus());
    }

    @Test
    @WithMockUser(username = "teacher@example.com")
    void testGetProjectAtSpecificRevision() {
        // Create project with multiple revisions
        Project project = Project.builder()
            .title("Revision Test Project")
            .status(EProjectStatus.ACTIVE)
            .year(2024)
            .semester(1)
            .build();
        
        project = projectRepository.save(project);
        Long projectId = project.getId();
        
        // Get revision number after creation
        List<Number> revisions = auditService.getRevisionNumbers(Project.class, projectId);
        int firstRevision = revisions.get(0).intValue();
        
        // Update status
        project.setStatus(EProjectStatus.LOCKED);
        projectRepository.save(project);
        
        revisions = auditService.getRevisionNumbers(Project.class, projectId);
        int secondRevision = revisions.get(1).intValue();
        
        // Retrieve project at first revision
        Project projectAtRev1 = auditService.getProjectAtRevision(projectId, firstRevision);
        assertEquals(EProjectStatus.ACTIVE, projectAtRev1.getStatus());
        
        // Retrieve project at second revision
        Project projectAtRev2 = auditService.getProjectAtRevision(projectId, secondRevision);
        assertEquals(EProjectStatus.LOCKED, projectAtRev2.getStatus());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void testTrackMultipleUpdates() {
        // Simulate a real workflow
        Project project = Project.builder()
            .title("Real Workflow Project")
            .content("Project description")
            .status(EProjectStatus.ACTIVE)
            .completionPercentage(0f)
            .year(2024)
            .semester(1)
            .build();
        
        // Day 1: Create project
        project = projectRepository.save(project);
        Long projectId = project.getId();
        
        // Day 2: Some progress
        project.setCompletionPercentage(25f);
        projectRepository.save(project);
        
        // Day 3: More progress
        project.setCompletionPercentage(50f);
        projectRepository.save(project);
        
        // Day 4: Almost done
        project.setCompletionPercentage(75f);
        projectRepository.save(project);
        
        // Day 5: Complete
        project.setStatus(EProjectStatus.COMPLETED);
        project.setCompletionPercentage(100f);
        projectRepository.save(project);
        
        // Verify all 5 revisions are tracked
        List<AuditRevisionDTO> history = auditService.getProjectHistory(projectId);
        assertEquals(5, history.size());
        
        // Verify progression
        assertEquals("INSERT", history.get(0).getRevisionType());
        assertEquals(0f, ((Project) history.get(0).getEntityData()).getCompletionPercentage());
        
        assertEquals(25f, ((Project) history.get(1).getEntityData()).getCompletionPercentage());
        assertEquals(50f, ((Project) history.get(2).getEntityData()).getCompletionPercentage());
        assertEquals(75f, ((Project) history.get(3).getEntityData()).getCompletionPercentage());
        assertEquals(100f, ((Project) history.get(4).getEntityData()).getCompletionPercentage());
        
        // All should be by same user
        assertTrue(history.stream()
            .allMatch(rev -> "admin@example.com".equals(rev.getUsername())));
    }

    @Test
    @WithMockUser(username = "student@example.com")
    void testAuditCapturesUsername() {
        Project project = Project.builder()
            .title("Username Capture Test")
            .status(EProjectStatus.ACTIVE)
            .year(2024)
            .semester(1)
            .build();
        
        project = projectRepository.save(project);
        
        List<AuditRevisionDTO> history = auditService.getProjectHistory(project.getId());
        
        // Verify username is captured
        assertEquals(1, history.size());
        assertEquals("student@example.com", history.get(0).getUsername());
    }
}
