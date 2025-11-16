package POSE_Project_Tracking.Blog.dto.res;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRes {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Author info
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    
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
