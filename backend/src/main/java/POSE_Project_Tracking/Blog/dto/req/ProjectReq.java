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
public class ProjectReq {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String content;

    private String objectives;

    @NotNull(message = "Năm không được để trống")
    private Integer year;

    @NotNull(message = "Học kỳ không được để trống")
    private Integer semester;

    private String faculty;

    private String batch;

    private LocalDate startDate;

    private LocalDate endDate;

    // List of student IDs to be added as project members
    private List<Long> studentIds;
}
