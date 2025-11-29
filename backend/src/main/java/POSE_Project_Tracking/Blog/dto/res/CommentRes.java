package POSE_Project_Tracking.Blog.dto.res;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRes {

    private Long id;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Lock information
    private Long lockedById;
    private String lockedByName;
    private LocalDateTime lockedAt;
    
    // Author info
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    
    // Creator information (from ProgressEntity)
    private Long createdById;
    private String createdByName;
    
    // Parent comment (if reply)
    private Long parentCommentId;
    
    // Related entity info
    private Long projectId;
    private Long milestoneId;
    private Long taskId;
    private Long reportId;
    
    // Statistics
    private Integer totalReplies;
}
