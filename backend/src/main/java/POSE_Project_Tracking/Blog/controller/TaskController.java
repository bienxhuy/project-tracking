package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.TaskReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.TaskRes;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import POSE_Project_Tracking.Blog.service.ITaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    // Tạo task mới
    @PostMapping
    public ApiResponse<TaskRes> createTask(@Valid @RequestBody TaskReq taskReq) {
        TaskRes task = taskService.createTask(taskReq);
        return new ApiResponse<>(HttpStatus.CREATED, "Tạo task thành công", task, null);
    }

    // Lấy task theo ID
    @GetMapping("/{id}")
    public ApiResponse<TaskRes> getTaskById(@PathVariable Long id) {
        TaskRes task = taskService.getTaskById(id);
        return new ApiResponse<>(HttpStatus.OK, "Lấy thông tin task thành công", task, null);
    }

    // Lấy tasks theo project
    @GetMapping("/project/{projectId}")
    public ApiResponse<List<TaskRes>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskRes> tasks = taskService.getTasksByProject(projectId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách task của dự án thành công", tasks, null);
    }

    // Lấy tasks theo user
    @GetMapping("/user/{userId}")
    public ApiResponse<List<TaskRes>> getTasksByUser(@PathVariable Long userId) {
        List<TaskRes> tasks = taskService.getTasksByUser(userId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách task của người dùng thành công", tasks, null);
    }

    // Lấy tasks theo status
    @GetMapping("/status/{status}")
    public ApiResponse<List<TaskRes>> getTasksByStatus(@PathVariable ETaskStatus status) {
        List<TaskRes> tasks = taskService.getTasksByStatus(status);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách task theo trạng thái thành công", tasks, null);
    }

    // Lấy tasks quá hạn
    @GetMapping("/overdue")
    public ApiResponse<List<TaskRes>> getOverdueTasks() {
        List<TaskRes> tasks = taskService.getOverdueTasks();
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách task quá hạn thành công", tasks, null);
    }

    // Lấy tasks quá hạn theo user
    @GetMapping("/overdue/user/{userId}")
    public ApiResponse<List<TaskRes>> getOverdueTasksByUser(@PathVariable Long userId) {
        List<TaskRes> tasks = taskService.getOverdueTasksByUser(userId);
        return new ApiResponse<>(HttpStatus.OK, "Lấy danh sách task quá hạn của người dùng thành công", tasks, null);
    }

    // Cập nhật task
    @PutMapping("/{id}")
    public ApiResponse<TaskRes> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskReq taskReq) {
        TaskRes task = taskService.updateTask(id, taskReq);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật task thành công", task, null);
    }

    // Gán task cho user
    @PatchMapping("/{taskId}/assign/{userId}")
    public ApiResponse<Void> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        taskService.assignTask(taskId, userId);
        return new ApiResponse<>(HttpStatus.OK, "Gán task thành công", null, null);
    }

    // Cập nhật status
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam ETaskStatus status) {
        taskService.updateTaskStatus(id, status);
        return new ApiResponse<>(HttpStatus.OK, "Cập nhật trạng thái task thành công", null, null);
    }

    // Đánh dấu task hoàn thành
    @PatchMapping("/{id}/complete")
    public ApiResponse<Void> markTaskAsCompleted(@PathVariable Long id) {
        taskService.markTaskAsCompleted(id);
        return new ApiResponse<>(HttpStatus.OK, "Đánh dấu task hoàn thành thành công", null, null);
    }

    // Xóa task
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ApiResponse<>(HttpStatus.OK, "Xóa task thành công", null, null);
    }
}
