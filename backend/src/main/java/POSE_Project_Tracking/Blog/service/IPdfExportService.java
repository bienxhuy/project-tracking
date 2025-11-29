package POSE_Project_Tracking.Blog.service;

import java.io.IOException;

/**
 * Service interface for exporting project data to PDF format
 */
public interface IPdfExportService {
    
    /**
     * Export complete project information to PDF format
     * 
     * @param projectId The ID of the project to export
     * @return byte array containing the generated PDF file
     * @throws IOException if PDF generation fails
     */
    byte[] exportProjectToPdf(Long projectId) throws IOException;
}
