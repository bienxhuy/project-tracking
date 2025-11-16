package POSE_Project_Tracking.Blog.dto.res;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentRes {

    private Long id;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String url;
    private LocalDateTime createdAt;
    
    // Uploaded by
    private Long uploadedById;
    private String uploadedByName;
    
    // Related entity info
    private Long projectId;
    private Long milestoneId;
    private Long taskId;
    private Long reportId;
    private Long commentId;
}
