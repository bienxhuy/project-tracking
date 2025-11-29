package POSE_Project_Tracking.Blog.dto.req;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkRegisterRequest {
    
    @NotEmpty(message = "Users list cannot be empty")
    @Valid
    private List<UserReq> users;
    
}
