package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.ReportReq;
import POSE_Project_Tracking.Blog.dto.req.UpdateReportReq;
import POSE_Project_Tracking.Blog.dto.res.ReportRes;
import POSE_Project_Tracking.Blog.entity.*;
import POSE_Project_Tracking.Blog.enums.EReportStatus;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.AttachmentMapper;
import POSE_Project_Tracking.Blog.mapper.ReportMapper;
import POSE_Project_Tracking.Blog.repository.*;
import POSE_Project_Tracking.Blog.service.ICommentService;
import POSE_Project_Tracking.Blog.service.IReportService;
import POSE_Project_Tracking.Blog.service.NotificationHelperService;
import POSE_Project_Tracking.Blog.enums.ENotificationType;
import POSE_Project_Tracking.Blog.util.FileUtil;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class ReportServiceImpl implements IReportService {

    // Allowed file types
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/zip",
            "application/x-zip-compressed"
    );

    // Maximum file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

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
    private AttachmentRepository attachmentRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private AttachmentUploadService attachmentUploadService;

    @Autowired
    @Lazy
    private ICommentService commentService;

    @Autowired
    private NotificationHelperService notificationHelperService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${upload.base-url:http://localhost:8080/api/v1/uploads}")
    private String baseUrl;

    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(FILE_UPLOAD_FAILED);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new CustomException(INVALID_FILE_TYPE);
        }
    }

    /**
     * Upload attachments for a report
     */
    private void uploadAttachments(Report report, MultipartFile[] attachments) {
        if (attachments == null || attachments.length == 0) {
            return;
        }

        User uploadedBy = securityUtil.getCurrentUser();
        List<Attachment> attachmentList = new ArrayList<>();

        for (MultipartFile file : attachments) {
            if (file.isEmpty()) {
                continue;
            }

            // Validate file
            validateFile(file);

            try {
                String fileName = file.getOriginalFilename();
                String url = attachmentUploadService.uploadAttachment(file);
                String fileType = file.getContentType();
                Long fileSize = file.getSize();

                // Create attachment entity
                // For Cloudinary storage, we use original filename as file_path since actual path is in the URL
                Attachment attachment = attachmentMapper.toEntity(
                        fileName,
                        fileName, // Use original filename for file_path (required by database)
                        fileType,
                        fileSize,
                        url,
                        report.getProject(),
                        report.getMilestone(),
                        report.getTask(),
                        report,
                        null,
                        uploadedBy
                );

                attachmentList.add(attachment);

            } catch (IOException e) {
                throw new CustomException(FILE_UPLOAD_FAILED);
            }
        }

        // Save all attachments
        if (!attachmentList.isEmpty()) {
            attachmentRepository.saveAll(attachmentList);
        }
    }

    @Override
    public ReportRes createReport(ReportReq reportReq) {
        return createReport(reportReq, null);
    }

    @Override
    public ReportRes createReport(ReportReq reportReq, MultipartFile[] attachments) {
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

        // Save report first
        report = reportRepository.save(report);

        // Upload attachments if provided
        uploadAttachments(report, attachments);

        // ✅ NOTIFICATION: Report được submit
        if (project != null) {
            try {
                String title = "Báo cáo mới được submit";
                String message = String.format("%s đã submit báo cáo \"%s\"", 
                    author.getDisplayName(), report.getTitle());
                
                notificationHelperService.createNotificationsForAllProjectMembers(
                    project,
                    title,
                    message,
                    ENotificationType.REPORT_SUBMITTED,
                    report.getId(),
                    "REPORT",
                    author
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Flush changes to database and clear persistence context
        entityManager.flush();
        entityManager.clear();

        // Reload report to get attachments with eager fetch
        report = reportRepository.findByIdWithAttachments(report.getId())
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

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
    public ReportRes updateReport(Long id, UpdateReportReq updateReportReq, MultipartFile[] files) {
        Report report = reportRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        // Only author can update their report
        User currentUser = securityUtil.getCurrentUser();
        if (!report.getAuthor().getId().equals(currentUser.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        // Update title and content
        report.setTitle(updateReportReq.getTitle());
        report.setContent(updateReportReq.getContent());

        // Handle attachment removal
        if (updateReportReq.getRemovedAttachmentIds() != null && !updateReportReq.getRemovedAttachmentIds().isEmpty()) {
            List<Attachment> attachmentsToRemove = report.getAttachments().stream()
                    .filter(att -> updateReportReq.getRemovedAttachmentIds().contains(att.getId()))
                    .collect(Collectors.toList());

            for (Attachment attachment : attachmentsToRemove) {
                report.getAttachments().remove(attachment);
                attachmentRepository.delete(attachment);
                // Delete physical file
                FileUtil.deleteFile(attachment.getFilePath());
            }
        }

        // Save report before uploading new attachments
        report = reportRepository.save(report);

        // Upload new attachments if provided (same pattern as createReport)
        uploadAttachments(report, files);

        // Flush changes to database and clear persistence context
        entityManager.flush();
        entityManager.clear();

        // Reload report to get attachments with eager fetch
        report = reportRepository.findByIdWithAttachments(report.getId())
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        return reportMapper.toResponse(report);
    }

    @Override
    public ReportRes getReportById(Long id, String include) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        if ("comments".equals(include)) {
            return reportMapper.toResponseWithComments(report);
        }
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
    public List<ReportRes> getReportsByTask(Long taskId, String include) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        if ("comments".equals(include)) {
            return reportRepository.findByTask(task).stream()
                    .map(reportMapper::toResponseWithComments)
                    .collect(Collectors.toList());
        }
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
        // Lock report and all children (comments)
        lockReportWithChildren(id);
    }

    @Override
    public void lockReportWithChildren(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        User currentUser = securityUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        // Lock report only
        report.setLocked(true);
        report.setLockedBy(currentUser);
        report.setLockedAt(now);
        report.setStatus(EReportStatus.LOCKED);
        reportRepository.save(report);

        // ✅ NOTIFICATION: Report bị khóa
        if (report.getProject() != null) {
            try {
                String title = "Báo cáo bị khóa";
                String message = String.format("Báo cáo \"%s\" đã bị khóa bởi giảng viên", report.getTitle());
                
                notificationHelperService.createNotificationsForStudentsOnly(
                    report.getProject(),
                    title,
                    message,
                    ENotificationType.REPORT_LOCKED,
                    report.getId(),
                    "REPORT",
                    currentUser
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Delegate to CommentService to lock all comments in report
        if (report.getComments() != null) {
            report.getComments().forEach(comment -> {
                commentService.lockComment(comment.getId());
            });
        }
    }
}
