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
public class BulkImportResult {
    
    private int total;
    private int success;
    private int failed;
    private List<BulkImportError> errors;
    private String taskId; // For cancelling email sending
    
}
