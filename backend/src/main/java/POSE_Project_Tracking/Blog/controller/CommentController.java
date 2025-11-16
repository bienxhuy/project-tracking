package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.CommentReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.CommentRes;
import POSE_Project_Tracking.Blog.service.ICommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    // Tạo comment mới
    @PostMapping
    public ApiResponse<CommentRes> createComment(@Valid @RequestBody CommentReq commentReq) {
        CommentRes comment = commentService.createComment(commentReq);
        return new ApiResponse<>(HttpStatus.CREATED, "Tạo bình luận thành công", comment, null);
    }

    // Lấy comment theo ID
    @GetMapping("/{id}")
    public ApiResponse<CommentRes> getCommentById(@PathVariable Long id) {
        CommentRes comment = commentService.getCommentById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin bình luận thành công", comment, null);
    }

    // Lấy comments theo project
    @GetMapping("/project/{projectId}")
    public ApiResponse<List<CommentRes>> getCommentsByProject(@PathVariable Long projectId) {
        List<CommentRes> comments = commentService.getCommentsByProject(projectId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách bình luận của dự án thành công", comments, null);
    }

    // Lấy comments theo task
    @GetMapping("/task/{taskId}")
    public ApiResponse<List<CommentRes>> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentRes> comments = commentService.getCommentsByTask(taskId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách bình luận của task thành công", comments, null);
    }

    // Lấy comments theo report
    @GetMapping("/report/{reportId}")
    public ApiResponse<List<CommentRes>> getCommentsByReport(@PathVariable Long reportId) {
        List<CommentRes> comments = commentService.getCommentsByReport(reportId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách bình luận của báo cáo thành công", comments, null);
    }

    // Lấy replies của comment
    @GetMapping("/{commentId}/replies")
    public ApiResponse<List<CommentRes>> getRepliesByComment(@PathVariable Long commentId) {
        List<CommentRes> replies = commentService                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   .getRepliesByComment(commentId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách phản hồi thành công", replies, null);
    }

    // Cập nhật comment
    @PutMapping("/{id}")
    public ApiResponse<CommentRes> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentReq commentReq) {
        CommentRes comment = commentService.updateComment(id, commentReq);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật bình luận thành công", comment, null);
    }

    // Xóa comment
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa bình luận thành công", null, null);
    }
}
