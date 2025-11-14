package POSE_Project_Tracking.Blog.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReq {

    @NotBlank(message = "Nội dung comment không được để trống")
    private String content;

    private Long projectId;

    private Long milestoneId;

    private Long taskId;

    private Long reportId;

    private Long parentCommentId;
}
