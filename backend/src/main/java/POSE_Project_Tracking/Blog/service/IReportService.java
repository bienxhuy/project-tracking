package POSE_Project_Tracking.Blog.service;

import POSE_Project_Tracking.Blog.dto.req.ReportReq;
import POSE_Project_Tracking.Blog.dto.res.ReportRes;
import POSE_Project_Tracking.Blog.enums.EReportStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportService {
    ReportRes createReport(ReportReq reportReq);
    ReportRes createReport(ReportReq reportReq, MultipartFile[] attachments);
    ReportRes updateReport(Long id, ReportReq reportReq);
    ReportRes getReportById(Long id, String include);
    List<ReportRes> getAllReports();
    List<ReportRes> getReportsByProject(Long projectId);
    List<ReportRes> getReportsByMilestone(Long milestoneId);
    List<ReportRes> getReportsByTask(Long taskId, String include);
    List<ReportRes> getReportsByAuthor(Long authorId);
    List<ReportRes> getReportsByUser(Long userId);  // Alias for getReportsByAuthor
    List<ReportRes> getReportsByStatus(EReportStatus status);
    List<ReportRes> getReportsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    void deleteReport(Long id);
    void submitReport(Long id);
    void lockReport(Long id);
    void lockReportWithChildren(Long id);
}
