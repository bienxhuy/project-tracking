package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.ReportReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.ReportRes;
import POSE_Project_Tracking.Blog.enums.EReportStatus;
import POSE_Project_Tracking.Blog.service.IReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private IReportService reportService;

    // Tạo report mới (với optional attachments)
    @PostMapping
    public ApiResponse<ReportRes> createReport(
            @Valid @ModelAttribute ReportReq reportReq,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments) {
        ReportRes report = reportService.createReport(reportReq, attachments);
        return new ApiResponse<>(HttpStatus.CREATED, "Tạo báo cáo thành công", report, null);
    }

    // Lấy report theo ID
    @GetMapping("/{id}")
    public ApiResponse<ReportRes> getReportById(
            @PathVariable Long id,
            @RequestParam(required = false) String include) {
        ReportRes report = reportService.getReportById(id, include);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin báo cáo thành công", report, null);
    }

    // Lấy reports theo project
    @GetMapping("/project/{projectId}")
    public ApiResponse<List<ReportRes>> getReportsByProject(@PathVariable Long projectId) {
        List<ReportRes> reports = reportService.getReportsByProject(projectId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách báo cáo của dự án thành công", reports, null);
    }

    // Lấy reports theo task
    @GetMapping("/task/{taskId}")
    public ApiResponse<List<ReportRes>> getReportsByTask(
            @PathVariable Long taskId,
            @RequestParam(required = false) String include) {
        List<ReportRes> reports = reportService.getReportsByTask(taskId, include);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách báo cáo của task thành công", reports, null);
    }

    // Lấy reports theo user
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ReportRes>> getReportsByUser(@PathVariable Long userId) {
        List<ReportRes> reports = reportService.getReportsByUser(userId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách báo cáo của người dùng thành công", reports, null);
    }

    // Lấy reports theo status
    @GetMapping("/status/{status}")
    public ApiResponse<List<ReportRes>> getReportsByStatus(@PathVariable EReportStatus status) {
        List<ReportRes> reports = reportService.getReportsByStatus(status);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách báo cáo theo trạng thái thành công", reports, null);
    }

    // Cập nhật report
    @PutMapping("/{id}")
    public ApiResponse<ReportRes> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody ReportReq reportReq) {
        ReportRes report = reportService.updateReport(id, reportReq);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật báo cáo thành công", report, null);
    }

    // Nộp report
    @PatchMapping("/{id}/submit")
    public ApiResponse<Void> submitReport(@PathVariable Long id) {
        reportService.submitReport(id);
        return new ApiResponse<>(HttpStatus.OK, "Nộp báo cáo thành công", null, null);
    }

    // Khóa report
    @PatchMapping("/{id}/lock")
    public ApiResponse<Void> lockReport(@PathVariable Long id) {
        reportService.lockReport(id);
        return new ApiResponse<>(HttpStatus.OK, "Khóa báo cáo thành công", null, null);
    }

    // Xóa report
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa báo cáo thành công", null, null);
    }
}
