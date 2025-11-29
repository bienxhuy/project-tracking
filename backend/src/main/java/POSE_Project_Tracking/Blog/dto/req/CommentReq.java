package POSE_Project_Tracking.Blog.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentReq {

    @NotBlank(message = "Nội dung comment không được để trống")
    private String content;

    private Long reportId;

    private Long parentCommentId;

    private List<Long> mentions; // Danh sách user IDs được mention
}
