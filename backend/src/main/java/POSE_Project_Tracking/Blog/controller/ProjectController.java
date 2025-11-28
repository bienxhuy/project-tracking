package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.ProjectReq;
import POSE_Project_Tracking.Blog.dto.req.UpdateContentReq;
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
    public ApiResponse<Void> lockProject(@PathVariable Long id) {
        projectService.lockProject(id);
        return new ApiResponse<>(HttpStatus.OK, "Khóa dự án thành công", null, null);
    }

    // Mở khóa project
    @PatchMapping("/{id}/unlock")
    public ApiResponse<Void> unlockProject(@PathVariable Long id) {
        projectService.unlockProject(id);
        return new ApiResponse<>(HttpStatus.OK, "Mở khóa dự án thành công", null, null);
    }

    // Lock project content (objective & description only)
    @Operation(summary = "Lock project content", 
               description = "Lock project objective and description (Instructor only)")
    @PatchMapping("/{id}/content/lock")
    public ApiResponse<Void> lockProjectContent(@PathVariable Long id) {
        projectService.lockProjectContent(id);
        return new ApiResponse<>(HttpStatus.OK, "Khóa nội dung dự án thành công", null, null);
    }

    // Unlock project content (objective & description only)
    @Operation(summary = "Unlock project content", 
               description = "Unlock project objective and description (Instructor only)")
    @PatchMapping("/{id}/content/unlock")
    public ApiResponse<Void> unlockProjectContent(@PathVariable Long id) {
        projectService.unlockProjectContent(id);
        return new ApiResponse<>(HttpStatus.OK, "Mở khóa nội dung dự án thành công", null, null);
    }

    // Update project content (objective & description only)
    @Operation(summary = "Update project objective and content", 
               description = "Update project objective and description (Student)")
    @PatchMapping("/{id}/content")
    public ApiResponse<ProjectRes> updateProjectContent(
            @PathVariable Long id,
            @RequestBody UpdateContentReq updateContentReq) {
        ProjectRes project = projectService.updateProjectContent(
                id, 
                updateContentReq.getObjective(), 
                updateContentReq.getContent()
        );
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật nội dung dự án thành công", project, null);
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

    // Lấy projects theo student (member)
    @Operation(summary = "Get projects by student with filters", 
               description = "Get all projects that a specific student is a member of, with optional year/semester/batch filters")
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<ProjectRes>> getProjectsByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String batch) {
        List<ProjectRes> projects = projectService.getProjectsByStudent(studentId, year, semester, batch);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án của sinh viên thành công", projects, null);
    }

    // Lấy projects theo student và status
    @Operation(summary = "Get projects by student and status with filters", 
               description = "Get projects that a specific student is a member of, filtered by status and optional year/semester/batch")
    @GetMapping("/student/{studentId}/status/{status}")
    public ApiResponse<List<ProjectRes>> getProjectsByStudentAndStatus(
            @PathVariable Long studentId,
            @PathVariable EProjectStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String batch) {
        List<ProjectRes> projects = projectService.getProjectsByStudentAndStatus(studentId, status, year, semester, batch);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án của sinh viên theo trạng thái thành công", projects, null);
    }

    // Lấy các dự án của current user (student đang đăng nhập)
    @Operation(summary = "Get my projects with filters", 
               description = "Get all projects that the current user is a member of, with optional year/semester/batch filters. Defaults to current academic period.")
    @GetMapping("/my-projects")
    public ApiResponse<List<ProjectRes>> getMyProjects(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String batch) {
        List<ProjectRes> projects = projectService.getMyProjects(year, semester, batch);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án của tôi thành công", projects, null);
    }

    // Lấy các dự án của current user theo status
    @Operation(summary = "Get my projects by status with filters", 
               description = "Get projects that the current user is a member of, filtered by status and optional year/semester/batch. Defaults to current academic period.")
    @GetMapping("/my-projects/status/{status}")
    public ApiResponse<List<ProjectRes>> getMyProjectsByStatus(
            @PathVariable EProjectStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String batch) {
        List<ProjectRes> projects = projectService.getMyProjectsByStatus(status, year, semester, batch);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án của tôi theo trạng thái thành công", projects, null);
    }

    // Lấy tất cả dự án với filters
    @Operation(summary = "Get all projects with filters", 
               description = "Get all projects with optional year/semester/batch filters. Defaults to current academic period.")
    @GetMapping("/filter")
    public ApiResponse<List<ProjectRes>> getAllProjectsWithFilters(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String batch) {
        List<ProjectRes> projects = projectService.getAllProjectsWithFilters(year, semester, batch);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách dự án với bộ lọc thành công", projects, null);
    }

    // ========== ENDPOINTS WITHOUT FILTERS (GET ALL) ==========

    // Lấy TẤT CẢ projects của student (không filter)
    @Operation(summary = "Get all projects by student (no filters)", 
               description = "Get ALL projects that a specific student is a member of, across all years/semesters/batches")
    @GetMapping("/student/{studentId}/all")
    public ApiResponse<List<ProjectRes>> getAllProjectsByStudent(@PathVariable Long studentId) {
        List<ProjectRes> projects = projectService.getAllProjectsByStudent(studentId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy tất cả dự án của sinh viên thành công", projects, null);
    }

    // Lấy TẤT CẢ projects của student theo status (không filter year/semester/batch)
    @Operation(summary = "Get all projects by student and status (no filters)", 
               description = "Get ALL projects that a specific student is a member of, filtered only by status")
    @GetMapping("/student/{studentId}/all/status/{status}")
    public ApiResponse<List<ProjectRes>> getAllProjectsByStudentAndStatus(
            @PathVariable Long studentId,
            @PathVariable EProjectStatus status) {
        List<ProjectRes> projects = projectService.getAllProjectsByStudentAndStatus(studentId, status);
        return new ApiResponse<>(HttpStatus.OK, "Lấy tất cả dự án của sinh viên theo trạng thái thành công", projects, null);
    }

    // Lấy TẤT CẢ projects của current user (không filter)
    @Operation(summary = "Get all my projects (no filters)", 
               description = "Get ALL projects that the current user is a member of, across all years/semesters/batches")
    @GetMapping("/my-projects/all")
    public ApiResponse<List<ProjectRes>> getAllMyProjects() {
        List<ProjectRes> projects = projectService.getAllMyProjects();
        return new ApiResponse<>(HttpStatus.OK, "Lấy tất cả dự án của tôi thành công", projects, null);
    }

    // Lấy TẤT CẢ projects của current user theo status (không filter year/semester/batch)
    @Operation(summary = "Get all my projects by status (no filters)", 
               description = "Get ALL projects that the current user is a member of, filtered only by status")
    @GetMapping("/my-projects/all/status/{status}")
    public ApiResponse<List<ProjectRes>> getAllMyProjectsByStatus(@PathVariable EProjectStatus status) {
        List<ProjectRes> projects = projectService.getAllMyProjectsByStatus(status);
        return new ApiResponse<>(HttpStatus.OK, "Lấy tất cả dự án của tôi theo trạng thái thành công", projects, null);
    }
}
