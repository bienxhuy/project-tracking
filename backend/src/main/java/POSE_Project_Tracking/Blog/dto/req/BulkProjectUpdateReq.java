package POSE_Project_Tracking.Blog.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for bulk updating a project with milestones and tasks
 * Used for AI-generated project structures
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkProjectUpdateReq {

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @NotBlank(message = "Mục tiêu không được để trống")
    private String objectives;

    @Valid
    @NotEmpty(message = "Phải có ít nhất một milestone")
    private List<BulkMilestoneReq> milestones;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkMilestoneReq {

        @NotBlank(message = "Tiêu đề milestone không được để trống")
        private String title;

        private String description;

        @NotNull(message = "Ngày bắt đầu không được để trống")
        private LocalDateTime startDate;

        @NotNull(message = "Ngày kết thúc không được để trống")
        private LocalDateTime endDate;

        @Valid
        private List<BulkTaskReq> tasks;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkTaskReq {

        @NotBlank(message = "Tiêu đề task không được để trống")
        private String title;

        private String description;

        @NotNull(message = "Ngày bắt đầu không được để trống")
        private LocalDateTime startDate;

        @NotNull(message = "Ngày kết thúc không được để trống")
        private LocalDateTime endDate;

        @Valid
        private List<AssigneeReq> assignees;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssigneeReq {

        @NotNull(message = "ID người dùng không được để trống")
        private Long id;

        private String displayName;
        private String email;
        private String role;
    }
}
