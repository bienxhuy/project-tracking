package POSE_Project_Tracking.Blog.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import POSE_Project_Tracking.Blog.dto.req.UserUpdateReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.service.IUserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    IUserService userService;


    // Lấy tất cả user với filters
    @GetMapping
    public ApiResponse<List<UserRes>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) String loginType
    ) {
        List<UserRes> users = userService.getAllUsers(search, role, accountStatus, loginType);
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

    // Cập nhật trạng thái user
    @PutMapping("/{id}/status")
    public ApiResponse<UserRes> updateUserStatus(@PathVariable Long id, @RequestParam EUserStatus status) {
        UserRes updated = userService.updateUserStatus(id, status);
        return new ApiResponse<>(HttpStatus.OK, "User status updated successfully", updated, null);
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

//    @GetMapping("/role-count")
//    public ApiResponse<Long> getRoleCount(@RequestParam EUserRole role) {
//        var result = userService.getCountOfUsers(role);
//        return new ApiResponse<>(HttpStatus.OK, "Get user count for role", result, null);
//    }
}
