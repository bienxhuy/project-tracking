package POSE_Project_Tracking.Blog.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ==========================
    // 1xxx - User-related errors
    // ==========================
    USER_EXIST(1001, "User already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must be at least 5 characters", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1003, "Invalid email address", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NON_EXISTENT(1005, "User does not exist", HttpStatus.NOT_FOUND),
    USERNAME_PASSWORD_INVALID(1006, "Username or password is incorrect", HttpStatus.UNAUTHORIZED),
    INVALID_USERID(1007, "User ID contains invalid characters", HttpStatus.BAD_REQUEST),
    INVALID_NAME(1009, "Name is invalid", HttpStatus.BAD_REQUEST),
    INVALID_ID(1010, "ID is invalid", HttpStatus.BAD_REQUEST),
    VERIFYING_EMAIL(1011, "Verifying email address", HttpStatus.FORBIDDEN),

    // ==============================
    // 2xxx - Authentication/Access
    // ==============================
    UNAUTHENTICATED(2001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "Unauthorized", HttpStatus.FORBIDDEN),

    // ====================
    // 3xxx - File Upload
    // ====================
    FILE_IMAGE_TOO_LARGE(3001, "File image is too large", HttpStatus.PAYLOAD_TOO_LARGE),
    UPLOAD_IMAGE_MUST_BE_IMAGE_TYPE(3002, "Uploaded file must be an image", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    EXCEED_MAXIMUM_ALLOW_NUMBER(3003, "Maximum of 5 image files allowed", HttpStatus.BAD_REQUEST),

    // ====================
    // 4xxx - Generic
    // ====================
    INVALID_KEY(4001, "Cannot find appropriate key", HttpStatus.NOT_FOUND),
    VALUE_NOT_FOUND(4002, "Value does not exist", HttpStatus.NOT_FOUND),

    // =======================
    // 5xxx - Project-related
    // =======================
    PROJECT_NOT_FOUND(5001, "Project does not exist", HttpStatus.NOT_FOUND),
    PROJECT_ALREADY_EXISTS(5002, "Project already exists", HttpStatus.BAD_REQUEST),
    PROJECT_NAME_INVALID(5003, "Project name is invalid", HttpStatus.BAD_REQUEST),
    PROJECT_LOCKED(5004, "Project is locked and cannot be modified", HttpStatus.FORBIDDEN),
    PROJECT_NOT_ACTIVE(5005, "Project is not active", HttpStatus.BAD_REQUEST),
    INVALID_PROJECT_STATUS(5006, "Invalid project status", HttpStatus.BAD_REQUEST),
    PROJECT_ACCESS_DENIED(5007, "You don't have permission to access this project", HttpStatus.FORBIDDEN),

    // ========================
    // 6xxx - Milestone-related
    // ========================
    MILESTONE_NOT_FOUND(6001, "Milestone does not exist", HttpStatus.NOT_FOUND),
    MILESTONE_ALREADY_EXISTS(6002, "Milestone already exists", HttpStatus.BAD_REQUEST),
    MILESTONE_NAME_INVALID(6003, "Milestone name is invalid", HttpStatus.BAD_REQUEST),
    MILESTONE_LOCKED(6004, "Milestone is locked and cannot be modified", HttpStatus.FORBIDDEN),
    INVALID_MILESTONE_STATUS(6005, "Invalid milestone status", HttpStatus.BAD_REQUEST),
    MILESTONE_DEADLINE_INVALID(6006, "Milestone deadline is invalid", HttpStatus.BAD_REQUEST),

    // ====================
    // 7xxx - Task-related
    // ====================
    TASK_NOT_FOUND(7001, "Task does not exist", HttpStatus.NOT_FOUND),
    TASK_ALREADY_EXISTS(7002, "Task already exists", HttpStatus.BAD_REQUEST),
    TASK_NAME_INVALID(7003, "Task name is invalid", HttpStatus.BAD_REQUEST),
    TASK_LOCKED(7004, "Task is locked and cannot be modified", HttpStatus.FORBIDDEN),
    INVALID_TASK_STATUS(7005, "Invalid task status", HttpStatus.BAD_REQUEST),
    TASK_DEADLINE_INVALID(7006, "Task deadline is invalid", HttpStatus.BAD_REQUEST),
    TASK_ASSIGNMENT_FAILED(7007, "Failed to assign task", HttpStatus.BAD_REQUEST),
    TASK_NOT_ASSIGNED_TO_USER(7008, "Task is not assigned to this user", HttpStatus.FORBIDDEN),

    // ======================
    // 8xxx - Report-related
    // ======================
    REPORT_NOT_FOUND(8001, "Report does not exist", HttpStatus.NOT_FOUND),
    REPORT_ALREADY_EXISTS(8002, "Report already exists", HttpStatus.BAD_REQUEST),
    REPORT_LOCKED(8003, "Report is locked and cannot be modified", HttpStatus.FORBIDDEN),
    INVALID_REPORT_STATUS(8004, "Invalid report status", HttpStatus.BAD_REQUEST),
    REPORT_CONTENT_INVALID(8005, "Report content is invalid", HttpStatus.BAD_REQUEST),
    REPORT_SUBMISSION_FAILED(8006, "Failed to submit report", HttpStatus.BAD_REQUEST),

    // ==========================
    // 9xxx - Notification-related
    // ==========================
    NOTIFICATION_NOT_FOUND(9001, "Notification does not exist", HttpStatus.NOT_FOUND),
    INVALID_NOTIFICATION_TYPE(9002, "Invalid notification type", HttpStatus.BAD_REQUEST),
    NOTIFICATION_SEND_FAILED(9003, "Failed to send notification", HttpStatus.INTERNAL_SERVER_ERROR),

    // =======================
    // 10xxx - Comment-related
    // =======================
    COMMENT_NOT_FOUND(10001, "Comment does not exist", HttpStatus.NOT_FOUND),
    COMMENT_CONTENT_INVALID(10002, "Comment content is invalid", HttpStatus.BAD_REQUEST),
    COMMENT_ACCESS_DENIED(10003, "You don't have permission to modify this comment", HttpStatus.FORBIDDEN),

    // ============================
    // 11xxx - Assignment-related
    // ============================
    ASSIGNMENT_NOT_FOUND(11001, "Assignment does not exist", HttpStatus.NOT_FOUND),
    USER_ALREADY_ASSIGNED(11002, "User is already assigned to this task/project", HttpStatus.BAD_REQUEST),
    ASSIGNMENT_FAILED(11003, "Failed to create assignment", HttpStatus.BAD_REQUEST),
    INSTRUCTOR_CANNOT_BE_ASSIGNED_TASK(11004, "Instructor cannot be assigned to tasks", HttpStatus.BAD_REQUEST),
    MEMBER_ALREADY_EXISTS(11005, "Member already exists in this project", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND(11006, "Member does not exist", HttpStatus.NOT_FOUND),

    // =======================
    // 12xxx - Deadline-related
    // =======================
    DEADLINE_PASSED(12001, "Deadline has already passed", HttpStatus.BAD_REQUEST),
    DEADLINE_INVALID(12002, "Deadline date is invalid", HttpStatus.BAD_REQUEST),
    DEADLINE_BEFORE_START_DATE(12003, "Deadline cannot be before start date", HttpStatus.BAD_REQUEST),

    // ==========================
    // 13xxx - Attachment-related
    // ==========================
    ATTACHMENT_NOT_FOUND(13001, "Attachment does not exist", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_FAILED(13002, "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED(13003, "Failed to delete file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE(13004, "Invalid file type", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(13005, "File size exceeds maximum limit", HttpStatus.PAYLOAD_TOO_LARGE);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return String.valueOf(code);
    }

}
