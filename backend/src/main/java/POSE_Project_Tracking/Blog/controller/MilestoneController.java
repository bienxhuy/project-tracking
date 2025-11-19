package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.MilestoneReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.MilestoneRes;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import POSE_Project_Tracking.Blog.service.IMilestoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/milestones")
public class MilestoneController {

    @Autowired
    private IMilestoneService milestoneService;

    // Tạo milestone mới
    @PostMapping
    public ApiResponse<MilestoneRes> createMilestone(@Valid @RequestBody MilestoneReq milestoneReq) {
        MilestoneRes milestone = milestoneService.createMilestone(milestoneReq);
        return new ApiResponse<>(HttpStatus.CREATED, "Tạo milestone thành công", milestone, null);
    }

    // Lấy milestone theo ID
    @GetMapping("/{id}")
    public ApiResponse<MilestoneRes> getMilestoneById(@PathVariable Long id) {
        MilestoneRes milestone = milestoneService.getMilestoneById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin milestone thành công", milestone, null);
    }

    // Lấy milestones theo project
    @GetMapping("/project/{projectId}")
    public ApiResponse<List<MilestoneRes>> getMilestonesByProject(@PathVariable Long projectId) {
        List<MilestoneRes> milestones = milestoneService.getMilestonesByProject(projectId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách milestone của dự án thành công", milestones, null);
    }

    // Lấy milestones theo status
    @GetMapping("/status/{status}")
    public ApiResponse<List<MilestoneRes>> getMilestonesByStatus(@PathVariable EMilestoneStatus status) {
        List<MilestoneRes> milestones = milestoneService.getMilestonesByStatus(status);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách milestone theo trạng thái thành công", milestones, null);
    }

    // Lấy milestones quá hạn
    @GetMapping("/overdue")
    public ApiResponse<List<MilestoneRes>> getOverdueMilestones() {
        List<MilestoneRes> milestones = milestoneService.getOverdueMilestones();
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách milestone quá hạn thành công", milestones, null);
    }

    // Cập nhật milestone
    @PutMapping("/{id}")
    public ApiResponse<MilestoneRes> updateMilestone(
            @PathVariable Long id,
            @Valid @RequestBody MilestoneReq milestoneReq) {
        MilestoneRes milestone = milestoneService.updateMilestone(id, milestoneReq);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật milestone thành công", milestone, null);
    }

    // Cập nhật status
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateMilestoneStatus(
            @PathVariable Long id,
            @RequestParam EMilestoneStatus status) {
        milestoneService.updateMilestoneStatus(id, status);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật trạng thái milestone thành công", null, null);
    }

    // Cập nhật completion percentage
    @PatchMapping("/{id}/completion")
    public ApiResponse<Void> updateMilestoneCompletion(@PathVariable Long id) {
        milestoneService.updateMilestoneCompletion(id);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật tiến độ milestone thành công", null, null);
    }

    // Xóa milestone
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMilestone(@PathVariable Long id) {
        milestoneService.deleteMilestone(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa milestone thành công", null, null);
    }
}
