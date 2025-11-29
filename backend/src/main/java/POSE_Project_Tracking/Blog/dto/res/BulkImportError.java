package POSE_Project_Tracking.Blog.dto.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportError {
    
    private int row;
    private String username;
    private String email;
    private List<String> errors;
    
}
