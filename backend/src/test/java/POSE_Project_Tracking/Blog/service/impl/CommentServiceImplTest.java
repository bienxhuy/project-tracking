package POSE_Project_Tracking.Blog.service.impl;

import java.util.Optional;

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

import POSE_Project_Tracking.Blog.dto.req.CommentReq;
import POSE_Project_Tracking.Blog.dto.res.CommentRes;
import POSE_Project_Tracking.Blog.entity.Comment;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.exceptionHandler.CustomException;
import POSE_Project_Tracking.Blog.mapper.CommentMapper;
import POSE_Project_Tracking.Blog.repository.CommentRepository;
import POSE_Project_Tracking.Blog.repository.MilestoneRepository;
import POSE_Project_Tracking.Blog.repository.ProjectRepository;
import POSE_Project_Tracking.Blog.repository.ReportRepository;
import POSE_Project_Tracking.Blog.repository.TaskRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.service.NotificationHelperService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private NotificationHelperService notificationHelperService;

    @InjectMocks
    private CommentServiceImpl service;

    private Comment comment;
    private Report report;
    private User author;
    private User otherUser;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);

        otherUser = new User();
        otherUser.setId(2L);

        report = new Report();
        report.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setAuthor(author);
        comment.setReport(report);
    }

    @Test
    void createComment_validRequest_createsComment() {
        CommentReq req = new CommentReq();
        req.setReportId(1L);
        req.setContent("New comment");

        when(securityUtil.getCurrentUser()).thenReturn(author);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(commentMapper.toEntity(any(), any(), any(), any())).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(new CommentRes());

        CommentRes result = service.createComment(req);

        assertNotNull(result);
        verify(commentRepository).save(comment);
    }

    @Test
    void createComment_reportNotFound_throwsException() {
        CommentReq req = new CommentReq();
        req.setReportId(999L);

        when(securityUtil.getCurrentUser()).thenReturn(author);
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.createComment(req));
    }

    @Test
    void updateComment_authorUpdatesOwnComment_updatesComment() {
        CommentReq req = new CommentReq();
        req.setContent("Updated content");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(securityUtil.getCurrentUser()).thenReturn(author);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(new CommentRes());

        CommentRes result = service.updateComment(1L, req);

        assertNotNull(result);
        verify(commentMapper).updateEntityFromRequest(req, comment);
        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_nonAuthorUpdates_throwsException() {
        CommentReq req = new CommentReq();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(securityUtil.getCurrentUser()).thenReturn(otherUser);

        assertThrows(CustomException.class, () -> service.updateComment(1L, req));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_authorDeletesOwnComment_deletesComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(securityUtil.getCurrentUser()).thenReturn(author);

        service.deleteComment(1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_nonAuthorDeletes_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(securityUtil.getCurrentUser()).thenReturn(otherUser);

        assertThrows(CustomException.class, () -> service.deleteComment(1L));
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void getCommentById_existingComment_returnsComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(comment)).thenReturn(new CommentRes());

        CommentRes result = service.getCommentById(1L);

        assertNotNull(result);
    }

    @Test
    void getCommentById_nonExistingComment_throwsException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.getCommentById(999L));
    }
}

