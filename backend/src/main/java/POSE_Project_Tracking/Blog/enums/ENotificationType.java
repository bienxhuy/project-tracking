package POSE_Project_Tracking.Blog.enums;

public enum ENotificationType {
    // Project notifications
    PROJECT_ASSIGNED,           // Sinh viên được thêm vào dự án
    PROJECT_CONTENT_DEFINED,    // Objective & content được define
    PROJECT_LOCKED,             // Project bị khóa
    PROJECT_CONTENT_LOCKED,     // Project content bị khóa
    
    // Task notifications
    TASK_ASSIGNED,              // Task được giao cho sinh viên
    TASK_COMPLETED,             // Task được đánh dấu hoàn thành
    TASK_LOCKED,                // Task bị khóa
    TASK_OVERDUE,               // Task quá hạn
    
    // Milestone notifications
    MILESTONE_LOCKED,           // Milestone bị khóa
    MILESTONE_DEADLINE_APPROACHING,  // Milestone sắp hết hạn (1 ngày)
    
    // Report notifications
    REPORT_SUBMITTED,           // Report được submit
    REPORT_LOCKED,              // Report bị khóa
    
    // Comment notifications
    COMMENT_ADDED,              // Comment được thêm vào task
    MENTION,                    // User được mention trong comment
    
    // Deadline notifications
    DEADLINE_APPROACHING,       // Deadline sắp tới (chung)
    PROJECT_DEADLINE_APPROACHING,   // Project sắp hết hạn (1 ngày)
    TASK_DEADLINE_APPROACHING       // Task sắp hết hạn (1 ngày)
}
