package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.AttachmentReq;
import POSE_Project_Tracking.Blog.dto.res.AttachmentRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAttachmentService {
    
    /**
     * Upload attachment cho report
     */
    AttachmentRes uploadAttachmentForReport(Long reportId, MultipartFile file);
    
    /**
     * Upload nhiều attachments cho report
     */
    List<AttachmentRes> uploadAttachmentsForReport(Long reportId, MultipartFile[] files);
    
    /**
     * Upload attachment cho comment
     */
    AttachmentRes uploadAttachmentForComment(Long commentId, MultipartFile file);
    
    /**
     * Upload attachment cho project/milestone/task với AttachmentReq
     */
    AttachmentRes uploadAttachment(AttachmentReq attachmentReq, MultipartFile file);
    
    /**
     * Lấy danh sách attachments của report
     */
    List<AttachmentRes> getAttachmentsByReportId(Long reportId);
    
    /**
     * Lấy attachment theo ID
     */
    AttachmentRes getAttachmentById(Long id);
    
    /**
     * Xóa attachment
     */
    void deleteAttachment(Long id);
}
