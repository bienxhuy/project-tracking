package POSE_Project_Tracking.Blog.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import POSE_Project_Tracking.Blog.dto.req.ReportReq;
import POSE_Project_Tracking.Blog.dto.res.ReportRes;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EReportStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ReportMapper;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ReportServiceImpl service;

    private Report report;
    private Project project;
    private User author;
    private User otherUser;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);

        otherUser = new User();
        otherUser.setId(2L);

        project = new Project();
        project.setId(1L);

        report = new Report();
        report.setId(1L);
        report.setTitle("Test Report");
        report.setAuthor(author);
        report.setProject(project);
        report.setStatus(EReportStatus.SUBMITTED);
    }

    @Test
    void createReport_validRequest_createsReport() {
        ReportReq req = new ReportReq();
        req.setProjectId(1L);

        when(securityUtil.getCurrentUser()).thenReturn(author);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(reportMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(report);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportMapper.toResponse(report)).thenReturn(new ReportRes());

        ReportRes result = service.createReport(req);

        assertNotNull(result);
        verify(reportRepository).save(report);
    }

    @Test
    void createReport_projectNotFound_throwsException() {
        ReportReq req = new ReportReq();
        req.setProjectId(999L);

        when(securityUtil.getCurrentUser()).thenReturn(author);
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.createReport(req));
    }

    @Test
    void updateReport_authorUpdatesDraftReport_updatesReport() {
        ReportReq req = new ReportReq();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(securityUtil.getCurrentUser()).thenReturn(author);
        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportMapper.toResponse(report)).thenReturn(new ReportRes());

        ReportRes result = service.updateReport(1L, req);

        assertNotNull(result);
        verify(reportMapper).updateEntityFromRequest(req, report);
        verify(reportRepository).save(report);
    }

    @Test
    void updateReport_nonAuthorUpdates_throwsException() {
        ReportReq req = new ReportReq();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(securityUtil.getCurrentUser()).thenReturn(otherUser);

        assertThrows(CustomException.class, () -> service.updateReport(1L, req));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void updateReport_approvedReport_throwsException() {
        report.setStatus(EReportStatus.APPROVED);
        ReportReq req = new ReportReq();

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(securityUtil.getCurrentUser()).thenReturn(author);

        assertThrows(CustomException.class, () -> service.updateReport(1L, req));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void deleteReport_authorDeletesDraftReport_deletesReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(securityUtil.getCurrentUser()).thenReturn(author);

        service.deleteReport(1L);

        verify(reportRepository).delete(report);
    }

    @Test
    void deleteReport_approvedReport_throwsException() {
        report.setStatus(EReportStatus.APPROVED);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(securityUtil.getCurrentUser()).thenReturn(author);

        assertThrows(CustomException.class, () -> service.deleteReport(1L));
        verify(reportRepository, never()).delete(any());
    }

    @Test
    void approveReport_existingReport_approvesReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        service.approveReport(1L);

        assertEquals(EReportStatus.APPROVED, report.getStatus());
        verify(reportRepository).save(report);
    }

    @Test
    void rejectReport_existingReport_rejectsReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        service.rejectReport(1L);

        assertEquals(EReportStatus.REJECTED, report.getStatus());
        verify(reportRepository).save(report);
    }

    @Test
    void submitReport_existingReport_submitsReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        service.submitReport(1L);

        assertEquals(EReportStatus.SUBMITTED, report.getStatus());
        assertNotNull(report.getSubmittedAt());
        verify(reportRepository).save(report);
    }

    @Test
    void getReportById_existingReport_returnsReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportMapper.toResponse(report)).thenReturn(new ReportRes());

        ReportRes result = service.getReportById(1L);

        assertNotNull(result);
    }

    @Test
    void getReportById_nonExistingReport_throwsException() {
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getReportById(999L));
    }
}

