package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.ENotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRes {

    private Long id;
    private String title;
    private String message;
    private ENotificationType type;
    private Boolean isRead;
    private Long referenceId;
    private String referenceType;
    private LocalDateTime createdAt;
    
    // Triggered by
    private Long triggeredById;
    private String triggeredByName;
}
