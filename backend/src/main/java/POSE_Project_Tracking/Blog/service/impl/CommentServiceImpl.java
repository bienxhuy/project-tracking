package POSE_Project_Tracking.Blog.service.impl;

import POSE_Project_Tracking.Blog.dto.req.CommentReq;
import POSE_Project_Tracking.Blog.dto.res.CommentRes;
import POSE_Project_Tracking.Blog.entity.Comment;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.CommentMapper;
import POSE_Project_Tracking.Blog.repository.CommentRepository;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.ICommentService;
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
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public CommentRes createComment(CommentReq commentReq) {
        // Lấy author (current user)
        User author = securityUtil.getCurrentUser();

        // Lấy project (nếu có)
        Project project = null;
        if (commentReq.getProjectId() != null) {
            project = projectRepository.findById(commentReq.getProjectId())
                    .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));
        }

        // Lấy milestone (nếu có)
        Milestone milestone = null;
        if (commentReq.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(commentReq.getMilestoneId())
                    .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));
        }

        // Lấy task (nếu có)
        Task task = null;
        if (commentReq.getTaskId() != null) {
            task = taskRepository.findById(commentReq.getTaskId())
                    .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));
        }

        // Lấy report (nếu có)
        Report report = null;
        if (commentReq.getReportId() != null) {
            report = reportRepository.findById(commentReq.getReportId())
                    .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));
        }

        // Lấy parent comment (nếu có)
        Comment parentComment = null;
        if (commentReq.getParentCommentId() != null) {
            parentComment = commentRepository.findById(commentReq.getParentCommentId())
                    .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        }

        // Map request to entity
        Comment comment = commentMapper.toEntity(commentReq, project, milestone, task, report, author, parentComment);

        // Save
        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    @Override
    public CommentRes updateComment(Long id, CommentReq commentReq) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // Only author can update their comment
        User currentUser = securityUtil.getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        // Update only allowed fields
        commentMapper.updateEntityFromRequest(commentReq, comment);

        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    @Override
    public CommentRes getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        return commentMapper.toResponse(comment);
    }

    @Override
    public List<CommentRes> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentRes> getCommentsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        return commentRepository.findByProject(project).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentRes> getCommentsByMilestone(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new CustomException(MILESTONE_NOT_FOUND));

        return commentRepository.findByMilestone(milestone).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentRes> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        return commentRepository.findByTask(task).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentRes> getCommentsByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(USER_NON_EXISTENT));

        return commentRepository.findByAuthor(author).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentRes> getCommentsByParent(Long parentId) {
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        return commentRepository.findByParentComment(parent).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        // Only author can delete their comment
        User currentUser = securityUtil.getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new CustomException(UNAUTHORIZED);
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentRes> getCommentsByReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(REPORT_NOT_FOUND));

        return commentRepository.findByReport(report).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentRes> getRepliesByComment(Long commentId) {
        // Alias for getCommentsByParent
        return getCommentsByParent(commentId);
    }

    @Override
    public void lockComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        User currentUser = securityUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        // Lock comment only
        comment.setLocked(true);
        comment.setLockedBy(currentUser);
        comment.setLockedAt(now);
        commentRepository.save(comment);
    }
}
