package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.ProjectRes;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import POSE_Project_Tracking.Blog.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project Management", description = "APIs for managing projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @Operation(summary = "Create new project", description = "Create a new project with the provided information")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "Project created successfully",
        content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @PostMapping
    public ApiResponse<ProjectRes> createProject(@Valid @RequestBody ProjectReq projectReq) {
        ProjectRes project = projectService.createProject(projectReq);
        return new ApiResponse<>(HttpStatus.CREATED, "Tạo dự án thành công", project, null);
    }

    @Operation(summary = "Get all projects", description = "Retrieve a list of all projects")
    @GetMapping
    public ApiResponse<List<ProjectRes>> getAllProjects() {
        List<ProjectRes> projects = projectService.getAllProjects();
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án thành công", projects, null);
    }

    @Operation(summary = "Get project by ID", description = "Retrieve detailed information about a specific project")
    @Parameter(name = "id", description = "Project ID", required = true)
    @GetMapping("/{id}")
    public ApiResponse<ProjectRes> getProjectById(@PathVariable Long id) {
        ProjectRes project = projectService.getProjectById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin dự án thành công", project, null);
    }

    // Lấy project với đầy đủ chi tiết (milestones, tasks, members)
    @GetMapping("/{id}/details")
    public ApiResponse<ProjectRes> getProjectWithDetails(@PathVariable Long id) {
        ProjectRes project = projectService.getProjectWithDetails(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy chi tiết dự án thành công", project, null);
    }

    // Lấy projects theo instructor
    @GetMapping("/instructor/{instructorId}")
    public ApiResponse<List<ProjectRes>> getProjectsByInstructor(@PathVariable Long instructorId) {
        List<ProjectRes> projects = projectService.getProjectsByInstructor(instructorId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án của giảng viên thành công", projects, null);
    }

    // Lấy projects theo status
    @GetMapping("/status/{status}")
    public ApiResponse<List<ProjectRes>> getProjectsByStatus(@PathVariable EProjectStatus status) {
        List<ProjectRes> projects = projectService.getProjectsByStatus(status);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án theo trạng thái thành công", projects, null);
    }

    // Lấy projects theo năm và học kỳ
    @GetMapping("/year/{year}/semester/{semester}")
    public ApiResponse<List<ProjectRes>> getProjectsByYearAndSemester(
            @PathVariable Integer year,
            @PathVariable Integer semester) {
        List<ProjectRes> projects = projectService.getProjectsByYearAndSemester(year, semester);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án theo năm và học kỳ thành công", projects, null);
    }

    // Tìm kiếm projects
    @GetMapping("/search")
    public ApiResponse<List<ProjectRes>> searchProjects(@RequestParam String keyword) {
        List<ProjectRes> projects = projectService.searchProjects(keyword);
        return new ApiResponse<>(HttpStatus.OK, "Tìm kiếm dự án thành công", projects, null);
    }

    // Cập nhật project
    @PutMapping("/{id}")
    public ApiResponse<ProjectRes> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectReq projectReq) {
        ProjectRes project = projectService.updateProject(id, projectReq);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật dự án thành công", project, null);
    }

    // Khóa project
    @PatchMapping("/{id}/lock")
    public ApiResponse<Void> lockProject(@PathVariable Long id, @RequestParam Long userId) {
        projectService.lockProject(id, userId);
        return new ApiResponse<>(HttpStatus.OK, "Khóa dự án thành công", null, null);
    }

    // Mở khóa project
    @PatchMapping("/{id}/unlock")
    public ApiResponse<Void> unlockProject(@PathVariable Long id) {
        projectService.unlockProject(id);
        return new ApiResponse<>(HttpStatus.OK, "Mở khóa dự án thành công", null, null);
    }

    // Cập nhật completion percentage
    @PatchMapping("/{id}/completion")
    public ApiResponse<Void> updateProjectCompletion(@PathVariable Long id) {
        projectService.updateProjectCompletion(id);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật tiến độ dự án thành công", null, null);
    }

    // Xóa project
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa dự án thành công", null, null);
    }
}
