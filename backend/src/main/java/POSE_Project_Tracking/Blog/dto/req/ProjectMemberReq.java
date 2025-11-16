package POSE_Project_Tracking.Blog.dto.req;

import POSE_Project_Tracking.Blog.enums.EUserRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberReq {

    @NotNull(message = "ID dự án không được để trống")
    private Long projectId;

    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;

    @NotNull(message = "Vai trò không được để trống")
    private EUserRole role;
}
