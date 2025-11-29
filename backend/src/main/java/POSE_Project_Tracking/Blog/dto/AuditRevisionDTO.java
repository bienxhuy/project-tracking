package POSE_Project_Tracking.Blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for audit/revision information
 * Used to return history of changes to the frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditRevisionDTO {
    
    /**
     * Revision number (sequential ID)
     */
    private Integer revisionNumber;
    
    /**
     * When the change was made
     */
    private LocalDateTime timestamp;
    
    /**
     * Who made the change
     */
    private String username;
    
    /**
     * IP address from which change was made
     */
    private String ipAddress;
    
    /**
     * Type of change: INSERT, UPDATE, or DELETE
     */
    private String revisionType;
    
    /**
     * The entity data at this revision
     */
    private Object entityData;
    
    /**
     * Changes made in this revision (field-by-field comparison)
     */
    private Object changes;
}
