package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.ReportReq;
import POSE_Project_Tracking.Blog.dto.res.ReportRes;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EReportStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.ReportMapper;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.IReportService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class ReportServiceImpl implements IReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public ReportRes createReport(ReportReq reportReq) {
        // Lấy author (current user)
        User author = securityUtil.getCurrentUser();

        // Lấy project (nếu có)
        Project project = null;
        if (reportReq.getProjectId() != null) {
            project = projectRepository.findById(reportReq.getProjectId())
                    .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));
        }

        // Lấy milestone (nếu có)
        Milestone milestone = null;
        if (reportReq.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(reportReq.getMilestoneId())
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
        }

        // Lấy task (nếu có)
        Task task = null;
        if (reportReq.getTaskId() != null) {
            task = taskRepository.findById(reportReq.getTaskId())
                    .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));
        }

        // Map request to entity
        Report report = reportMapper.toEntity(reportReq, project, milestone, task, author);

        // Save
        report = reportRepository.save(report);

        return reportMapper.toResponse(report);
    }

    @Override
    public ReportRes updateReport(Long id, ReportReq reportReq) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        // Only author can update their report
        User currentUser = securityUtil.getCurrentUser();
        if (!report.getAuthor().getId().equals(currentUser.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        // Update only allowed fields
        reportMapper.updateEntityFromRequest(reportReq, report);

        report = reportRepository.save(report);

        return reportMapper.toResponse(report);
    }

    @Override
    public ReportRes getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        return reportMapper.toResponse(report);
    }

    @Override
    public List<ReportRes> getAllReports() {
        return reportRepository.findAll().stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportRes> getReportsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return reportRepository.findByProject(project).stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportRes> getReportsByMilestone(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        return reportRepository.findByMilestone(milestone).stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportRes> getReportsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        return reportRepository.findByTask(task).stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportRes> getReportsByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return reportRepository.findByAuthor(author).stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportRes> getReportsByStatus(EReportStatus status) {
        return reportRepository.findByStatus(status).stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportRes> getReportsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.findByReportDateBetween(startDate, endDate).stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        // Only author can delete their report
        User currentUser = securityUtil.getCurrentUser();
        if (!report.getAuthor().getId().equals(currentUser.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        // Cannot delete if already locked
        if (Boolean.TRUE.equals(report.getLocked())) {
            throw new CustomException(REPORT_LOCKED);
        }

        reportRepository.delete(report);
    }

    @Override
    public List<ReportRes> getReportsByUser(Long userId) {
        // Alias for getReportsByAuthor
        return getReportsByAuthor(userId);
    }
    @Override
    public void submitReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        report.setStatus(EReportStatus.SUBMITTED);
        report.setSubmittedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    @Override
    public void lockReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        report.setStatus(EReportStatus.LOCKED);
        report.setLocked(true);
        report.setLockedAt(LocalDateTime.now());
        
        // Get current user as the one who locked
        User currentUser = securityUtil.getCurrentUser();
        report.setLockedBy(currentUser);
        
        reportRepository.save(report);
    }
}
