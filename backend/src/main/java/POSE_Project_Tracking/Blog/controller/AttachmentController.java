package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.AttachmentReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.AttachmentRes;
import POSE_Project_Tracking.Blog.service.IAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attachments")
@Tag(name = "Attachment", description = "API quản lý tệp đính kèm")
public class AttachmentController {

    @Autowired
    private IAttachmentService attachmentService;

    @PostMapping("/report/{reportId}")
    @Operation(summary = "Upload file đính kèm cho report", description = "Upload một file đính kèm cho report")
    public ApiResponse<AttachmentRes> uploadAttachmentForReport(
            @PathVariable Long reportId,
            @RequestParam("file") MultipartFile file) {
        AttachmentRes attachment = attachmentService.uploadAttachmentForReport(reportId, file);
        return new ApiResponse<>(HttpStatus.CREATED, "Upload file thành công", attachment, null);
    }

    @PostMapping("/report/{reportId}/multiple")
    @Operation(summary = "Upload nhiều file đính kèm cho report", description = "Upload nhiều file đính kèm cùng lúc cho report")
    public ApiResponse<List<AttachmentRes>> uploadAttachmentsForReport(
            @PathVariable Long reportId,
            @RequestParam("files") MultipartFile[] files) {
        List<AttachmentRes> attachments = attachmentService.uploadAttachmentsForReport(reportId, files);
        return new ApiResponse<>(HttpStatus.CREATED, "Upload files thành công", attachments, null);
    }

    @PostMapping("/comment/{commentId}")
    @Operation(summary = "Upload file đính kèm cho comment", description = "Upload một file đính kèm cho comment")
    public ApiResponse<AttachmentRes> uploadAttachmentForComment(
            @PathVariable Long commentId,
            @RequestParam("file") MultipartFile file) {
        AttachmentRes attachment = attachmentService.uploadAttachmentForComment(commentId, file);
        return new ApiResponse<>(HttpStatus.CREATED, "Upload file cho comment thành công", attachment, null);
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload file đính kèm với thông tin tùy chỉnh", 
               description = "Upload file với khả năng chỉ định project/milestone/task/report/comment")
    public ApiResponse<AttachmentRes> uploadAttachment(
            @ModelAttribute AttachmentReq attachmentReq,
            @RequestParam("file") MultipartFile file) {
        AttachmentRes attachment = attachmentService.uploadAttachment(attachmentReq, file);
        return new ApiResponse<>(HttpStatus.CREATED, "Upload file thành công", attachment, null);
    }

    @GetMapping("/report/{reportId}")
    @Operation(summary = "Lấy danh sách file đính kèm của report", description = "Lấy tất cả file đính kèm thuộc về một report")
    public ApiResponse<List<AttachmentRes>> getAttachmentsByReportId(@PathVariable Long reportId) {
        List<AttachmentRes> attachments = attachmentService.getAttachmentsByReportId(reportId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách file đính kèm thành công", attachments, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin file đính kèm", description = "Lấy thông tin chi tiết của một file đính kèm")
    public ApiResponse<AttachmentRes> getAttachmentById(@PathVariable Long id) {
        AttachmentRes attachment = attachmentService.getAttachmentById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin file đính kèm thành công", attachment, null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file đính kèm", description = "Xóa file đính kèm (chỉ người upload hoặc admin)")
    public ApiResponse<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa file đính kèm thành công", null, null);
    }
}
