package POSE_Project_Tracking.Blog.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportReq {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    // List of existing attachment IDs to keep
    private List<Long> existingAttachmentIds;

    // List of attachment IDs to remove
    private List<Long> removedAttachmentIds;
}
