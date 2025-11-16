package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.CommentReq;
import POSE_Project_Tracking.Blog.dto.res.CommentRes;

import java.util.List;

public interface ICommentService {
    CommentRes createComment(CommentReq commentReq);
    CommentRes updateComment(Long id, CommentReq commentReq);
    CommentRes getCommentById(Long id);
    List<CommentRes> getAllComments();
    List<CommentRes> getCommentsByProject(Long projectId);
    List<CommentRes> getCommentsByMilestone(Long milestoneId);
    List<CommentRes> getCommentsByTask(Long taskId);
    List<CommentRes> getCommentsByReport(Long reportId);
    List<CommentRes> getCommentsByAuthor(Long authorId);
    List<CommentRes> getCommentsByParent(Long parentId);
    List<CommentRes> getRepliesByComment(Long commentId);  // Alias for getCommentsByParent
    void deleteComment(Long id);
}
