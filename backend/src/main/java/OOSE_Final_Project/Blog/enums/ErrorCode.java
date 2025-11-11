package OOSE_Final_Project.Blog.enums;

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
    VALUE_NOT_FOUND(4002, "Value does not exist", HttpStatus.NOT_FOUND);

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
