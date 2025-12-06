package POSE_Project_Tracking.Blog.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import POSE_Project_Tracking.Blog.dto.req.BulkRegisterRequest;
import POSE_Project_Tracking.Blog.dto.res.BulkImportError;
import POSE_Project_Tracking.Blog.dto.res.BulkImportResult;
import POSE_Project_Tracking.Blog.facade.UserOTPFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.req.UserUpdateReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.service.IUserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    UserOTPFacade userOTPFacade;


    // Lấy tất cả user với filters
    @GetMapping
    public ApiResponse<List<UserRes>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role
    ) {
        List<UserRes> users = userService.getAllUsers(search, role);
        return new ApiResponse<>(HttpStatus.OK, "Fetched all users", users, null);
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ApiResponse<UserRes> getUserById(@PathVariable Long id) {
        UserRes user = userService.getUserById(id);
        return new ApiResponse<>(HttpStatus.OK, "Fetched user by ID", user, null);
    }

    // Lấy user theo username
    @GetMapping("/username/{username}")
    public ApiResponse<UserRes> getUserByUsername(@PathVariable String username) {
        UserRes user = userService.getUserByUsername(username);
        return new ApiResponse<>(HttpStatus.OK, "Fetched user by username", user, null);
    }

    // Lấy user theo email
    @GetMapping("/email/{email}")
    public ApiResponse<UserRes> getUserByEmail(@PathVariable String email) {
        UserRes user = userService.getUserByEmail(email);
        return new ApiResponse<>(HttpStatus.OK, "Fetched user by email", user, null);
    }

    // Cập nhật thông tin user
    @PutMapping("/{id}")
    public ApiResponse<UserRes> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateReq userDetails) {
        UserRes updated = userService.updateUser(id, userDetails);

        return new ApiResponse<>(HttpStatus.OK, "User updated successfully", updated, null);
    }

    @PutMapping("/token/{id}")
    public ApiResponse<String> updateUserToken(@PathVariable Long id, @Valid @RequestBody UserUpdateReq userDetails) {
        var result = userService.updateUserToken(id, userDetails);

        return new ApiResponse<>(HttpStatus.OK, "User updated successfully and return token", result, null);
    }

    // Cập nhật thông tin user
    @PutMapping("/email/{email}")
    public ApiResponse<UserRes> updateUser(@PathVariable String email, @Valid @RequestBody UserUpdateReq userDetails) {
        var user = userService.findByUsernameOrEmail(email);
        UserRes updated = userService.updateUser(user.getId(), userDetails);
        return new ApiResponse<>(HttpStatus.OK, "User updated successfully", updated, null);
    }

    // Xoá user
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ApiResponse<>(HttpStatus.NO_CONTENT, "User deleted successfully", null, null);
    }

    @GetMapping("/listId")
    public ApiResponse<List<UserRes>> getAllUserIds(
            @RequestParam List<Long> authorIds
    ) {
        var result = userService.findUsersByIds(authorIds);
        return new ApiResponse<>(HttpStatus.OK, "Fetched all users by ids", result, null);
    }

    // Create single user (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Admin only - Create a single user account")
    public ApiResponse<UserRes> createUser(@Valid @RequestBody UserReq userReq) {
        UserRes createdUser = userOTPFacade.createUser(userReq);
        return new ApiResponse<>(HttpStatus.CREATED, "User created successfully. Verification email sent.", createdUser, null);
    }

    // Bulk import users (Admin only)
    @PostMapping("/bulk-import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bulk import users", description = "Admin only - Create multiple users at once")
    @Transactional
    public ApiResponse<BulkImportResult> bulkImportUsers(@Valid @RequestBody BulkRegisterRequest request) {
        List<UserRes> createdUsers = new ArrayList<>();
        List<BulkImportError> errors = new ArrayList<>();
        List<String> originalPasswords = new ArrayList<>();
        
        // Step 1: Validate and create all users (without sending emails)
        for (int i = 0; i < request.getUsers().size(); i++) {
            UserReq userReq = request.getUsers().get(i);
            try {
                // Store original password before hashing
                String originalPassword = userReq.getPassword();
                
                // Create user without sending email
                UserRes createdUser = userService.createUser(userReq);
                createdUsers.add(createdUser);
                originalPasswords.add(originalPassword);
            } catch (Exception e) {
                // Provide user-friendly error messages
                String errorMessage = e.getMessage();
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "Failed to create user due to unknown error";
                } else if (errorMessage.contains("username") || errorMessage.contains("UK_USERNAME")) {
                    errorMessage = "Username '" + userReq.getUsername() + "' is already taken";
                } else if (errorMessage.contains("email") || errorMessage.contains("UK_EMAIL")) {
                    errorMessage = "Email '" + userReq.getEmail() + "' is already registered";
                } else if (errorMessage.contains("Duplicate")) {
                    errorMessage = "This user information is already in use";
                } else if (errorMessage.contains("ConstraintViolation")) {
                    errorMessage = "Invalid data format. Please check the input fields";
                }
                
                errors.add(new BulkImportError(
                    i + 1,
                    userReq.getUsername(),
                    userReq.getEmail(),
                    List.of(errorMessage)
                ));
            }
        }
        
        // Step 2: Send batch verification emails asynchronously with taskId
        String taskId = null;
        if (!createdUsers.isEmpty()) {
            taskId = java.util.UUID.randomUUID().toString();
            userOTPFacade.sendBatchAccountInfoEmails(taskId, createdUsers, originalPasswords);
        }
        
        BulkImportResult result = new BulkImportResult(
            request.getUsers().size(),
            createdUsers.size(),
            errors.size(),
            errors,
            taskId
        );
        
        return new ApiResponse<>(
            HttpStatus.CREATED,
            "Bulk import completed",
            result,
            null
        );
    }
    
    // Cancel bulk import email sending (Admin only)
    @PostMapping("/bulk-import/cancel/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> cancelBulkImportEmails(@PathVariable String taskId) {
        userOTPFacade.cancelEmailBatchTask(taskId);
        return new ApiResponse<>(
            HttpStatus.OK,
            "Email sending cancelled",
            null,
            null
        );
    }

//    @GetMapping("/role-count")
//    public ApiResponse<Long> getRoleCount(@RequestParam EUserRole role) {
//        var result = userService.getCountOfUsers(role);
//        return new ApiResponse<>(HttpStatus.OK, "Get user count for role", result, null);
//    }
}
