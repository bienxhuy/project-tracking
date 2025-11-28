package POSE_Project_Tracking.Blog.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskReq {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    @NotNull(message = "ID dự án không được để trống")
    private Long projectId;

    private Long milestoneId;

    // Multiple assignees support
    private List<Long> assigneeIds;

    private LocalDate startDate;

    private LocalDate endDate;
}
