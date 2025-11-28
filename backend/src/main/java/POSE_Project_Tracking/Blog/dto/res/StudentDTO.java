package POSE_Project_Tracking.Blog.dto.res;

import POSE_Project_Tracking.Blog.enums.EUserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    
    private Long id;
    
    @JsonProperty("full_name")
    private String fullName;
    
    private String email;
    
    private EUserRole role;
}
