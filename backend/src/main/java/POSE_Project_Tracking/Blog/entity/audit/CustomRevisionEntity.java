package POSE_Project_Tracking.Blog.entity.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * Custom Revision Entity to store additional audit information
 * 
 * This entity extends DefaultRevisionEntity to add custom fields:
 * - username: Who made the change
 * - ipAddress: From which IP address
 * 
 * Envers will automatically create REVINFO table with these fields
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "REVINFO")
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity {
    
    /**
     * Username of the person who made the change
     * Automatically captured from SecurityContext
     */
    private String username;
    
    /**
     * IP address from which the change was made
     * Useful for security audit
     */
    private String ipAddress;
    
    /**
     * Additional info about the action performed
     * E.g., "Status changed", "Grade updated", etc.
     */
    private String action;
}
