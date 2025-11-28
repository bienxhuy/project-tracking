package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.AttachmentReq;
import POSE_Project_Tracking.Blog.dto.res.AttachmentRes;
import POSE_Project_Tracking.Blog.entity.*;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.AttachmentMapper;
import POSE_Project_Tracking.Blog.repository.*;
import POSE_Project_Tracking.Blog.service.IAttachmentService;
import POSE_Project_Tracking.Blog.util.FileUtil;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static POSE_Project_Tracking.Blog.enums.ErrorCode.*;

@Service
@Transactional(rollbackOn = Exception.class)
public class AttachmentServiceImpl implements IAttachmentService {

    // Allowed file types
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/zip",
            "application/x-zip-compressed"
    );

    // Maximum file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB in bytes

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private AttachmentUploadService attachmentUploadService;

    @Value("${upload.base-url:http://localhost:8080/api/v1/uploads}")
    private String baseUrl;

    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new CustomException(FILE_UPLOAD_FAILED);
        }

        // Check file size (50MB max)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(FILE_TOO_LARGE);
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new CustomException(INVALID_FILE_TYPE);
        }
    }

    @Override
    public AttachmentRes uploadAttachmentForReport(Long reportId, MultipartFile file) {
        // Validate file
        validateFile(file);

        // Validate report exists
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        // Get current user
        User uploadedBy = securityUtil.getCurrentUser();

        // Upload file to Cloudinary
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
                    report.getProject(), // Link to project from report
                    report.getMilestone(),
                    report.getTask(),
                    report,
                    null, // No comment
                    uploadedBy
            );

            // Save
            attachment = attachmentRepository.save(attachment);

            return attachmentMapper.toResponse(attachment);

        } catch (IOException e) {
            throw new CustomException(FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public List<AttachmentRes> uploadAttachmentsForReport(Long reportId, MultipartFile[] files) {
        List<AttachmentRes> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                AttachmentRes attachment = uploadAttachmentForReport(reportId, file);
                attachments.add(attachment);
            }
        }

        return attachments;
    }

    @Override
    public AttachmentRes uploadAttachmentForComment(Long commentId, MultipartFile file) {
        // Validate file
        validateFile(file);

        // Validate comment exists
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // Get current user
        User uploadedBy = securityUtil.getCurrentUser();

        // Upload file to Cloudinary
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
                    comment.getProject(),
                    comment.getMilestone(),
                    comment.getTask(),
                    comment.getReport(),
                    comment,
                    uploadedBy
            );

            // Save
            attachment = attachmentRepository.save(attachment);

            return attachmentMapper.toResponse(attachment);

        } catch (IOException e) {
            throw new CustomException(FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public AttachmentRes uploadAttachment(AttachmentReq attachmentReq, MultipartFile file) {
        // Validate file
        validateFile(file);

        // Get current user
        User uploadedBy = securityUtil.getCurrentUser();

        // Validate and get related entities
        Project project = null;
        if (attachmentReq.getProjectId() != null) {
            project = projectRepository.findById(attachmentReq.getProjectId())
                    .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));
        }

        Milestone milestone = null;
        if (attachmentReq.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(attachmentReq.getMilestoneId())
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
        }

        Task task = null;
        if (attachmentReq.getTaskId() != null) {
            task = taskRepository.findById(attachmentReq.getTaskId())
                    .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));
        }

        Report report = null;
        if (attachmentReq.getReportId() != null) {
            report = reportRepository.findById(attachmentReq.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
        }

        Comment comment = null;
        if (attachmentReq.getCommentId() != null) {
            comment = commentRepository.findById(attachmentReq.getCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        }

        // Upload file to Cloudinary
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
                    project,
                    milestone,
                    task,
                    report,
                    comment,
                    uploadedBy
            );

            // Save
            attachment = attachmentRepository.save(attachment);

            return attachmentMapper.toResponse(attachment);

        } catch (IOException e) {
            throw new CustomException(FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public List<AttachmentRes> getAttachmentsByReportId(Long reportId) {
        List<Attachment> attachments = attachmentRepository.findByReportId(reportId);
        return attachments.stream()
                .map(attachmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttachmentRes getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ATTACHMENT_NOT_FOUND));
        return attachmentMapper.toResponse(attachment);
    }

    @Override
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ATTACHMENT_NOT_FOUND));

        // Check permission - only uploader or admin can delete
        User currentUser = securityUtil.getCurrentUser();
        if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
            // TODO: Add admin role check
            throw new CustomException(UNAUTHORIZED);
        }

        // Delete file from storage
        FileUtil.deleteFile(attachment.getFilePath());

        // Delete from database
        attachmentRepository.delete(attachment);
    }
}
