package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.ProjectMemberReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.ProjectMemberRes;
import POSE_Project_Tracking.Blog.service.IProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project-members")
public class ProjectMemberController {

    @Autowired
    private IProjectMemberService projectMemberService;

    // Thêm member vào project
    @PostMapping
    public ApiResponse<ProjectMemberRes> addMember(@Valid @RequestBody ProjectMemberReq memberReq) {
        ProjectMemberRes member = projectMemberService.addMember(memberReq);
        return new ApiResponse<>(HttpStatus.CREATED, "Thêm thành viên vào dự án thành công", member, null);
    }

    // Lấy member theo ID
    @GetMapping("/{id}")
    public ApiResponse<ProjectMemberRes> getMemberById(@PathVariable Long id) {
        ProjectMemberRes member = projectMemberService.getMemberById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin thành viên thành công", member, null);
    }

    // Lấy members theo project
    @GetMapping("/project/{projectId}")
    public ApiResponse<List<ProjectMemberRes>> getMembersByProject(@PathVariable Long projectId) {
        List<ProjectMemberRes> members = projectMemberService.getMembersByProject(projectId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách thành viên của dự án thành công", members, null);
    }

    // Lấy projects theo user
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ProjectMemberRes>> getProjectsByUser(@PathVariable Long userId) {
        List<ProjectMemberRes> projects = projectMemberService.getProjectsByUser(userId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án của người dùng thành công", projects, null);
    }

    // Cập nhật member
    @PutMapping("/{id}")
    public ApiResponse<ProjectMemberRes> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody ProjectMemberReq memberReq) {
        ProjectMemberRes member = projectMemberService.updateMember(id, memberReq);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật thành viên thành công", member, null);
    }

    // Vô hiệu hóa member
    @PatchMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateMember(@PathVariable Long id) {
        projectMemberService.deactivateMember(id);
        return new ApiResponse<>(HttpStatus.OK, "Vô hiệu hóa thành viên thành công", null, null);
    }

    // Kích hoạt member
    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activateMember(@PathVariable Long id) {
        projectMemberService.activateMember(id);
        return new ApiResponse<>(HttpStatus.OK, "Kích hoạt thành viên thành công", null, null);
    }

    // Xóa member khỏi project
    @DeleteMapping("/{id}")
    public ApiResponse<Void> removeMember(@PathVariable Long id) {
        projectMemberService.removeMember(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa thành viên khỏi dự án thành công", null, null);
    }
}
