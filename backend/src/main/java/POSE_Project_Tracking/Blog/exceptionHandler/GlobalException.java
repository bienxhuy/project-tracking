package POSE_Project_Tracking.Blog.exceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import POSE_Project_Tracking.Blog.dto.res.ApiResponse;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(NoSuchElementException ex) {
        var result = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "HandleNotFound", ex.getMessage(),
                                       "ENTITY_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        var result = new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Authentication failed", ex.getMessage(), "BAD_CREDENTIALS");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        var result = new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid argument", ex.getMessage(), "INVALID_ARGUMENT");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(InternalAuthenticationServiceException ex) {
        var result = new ApiResponse<>(HttpStatus.BAD_REQUEST, "HandleNotFound", null, "AUTHENTICATION_FAILURE");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllException(Exception ex) {
        var result = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Handle All exception", ex.getMessage(), "INTERNAL_SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid request content", errorList, "VALIDATION_ERROR");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException ex) {
        var errorCode = ex.getErrorCode();
        var result = new ApiResponse<>(
                errorCode.getHttpStatus(),
                "Business Logic Error",
                ex.getMessage(),
                errorCode.getCode()
        );
        return ResponseEntity.status(errorCode.getHttpStatus()).body(result);
    }
    
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        String userMessage = "Unable to process request due to data conflict. ";
        
        // Parse specific constraint violations
        String exMessage = ex.getMessage();
        if (exMessage != null) {
            if (exMessage.contains("username") || exMessage.contains("UK_USERNAME")) {
                userMessage = "This username is already taken. Please use a different username.";
            } else if (exMessage.contains("email") || exMessage.contains("UK_EMAIL")) {
                userMessage = "This email address is already registered. Please use a different email.";
            } else if (exMessage.contains("Duplicate entry")) {
                userMessage = "This information is already in use. Please check your input and try again.";
            }
        }
        
        var result = new ApiResponse<>(HttpStatus.CONFLICT, userMessage, null, "DATA_CONFLICT");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }
    
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        
        var result = new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid data provided. Please check your input.", errors, "VALIDATION_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    @ExceptionHandler(org.springframework.transaction.TransactionException.class)
    public ResponseEntity<ApiResponse<?>> handleTransactionException(org.springframework.transaction.TransactionException ex) {
        var result = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, 
            "Unable to complete the operation. Please try again later.", 
            null, 
            "TRANSACTION_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

}
