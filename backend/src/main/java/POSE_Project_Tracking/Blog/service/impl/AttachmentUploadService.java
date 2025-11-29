package POSE_Project_Tracking.Blog.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service for uploading non-image files (PDF, DOC, DOCX, ZIP, etc.) to Cloudinary
 */
@Service
public class AttachmentUploadService {

    private final Cloudinary cloudinary;

    public AttachmentUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload attachment file to Cloudinary
     * Supports PDF, DOC, DOCX, ZIP and other non-image files
     * 
     * @param file MultipartFile to upload
     * @return URL of uploaded file on Cloudinary
     * @throws IOException if upload fails
     */
    public String uploadAttachment(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IOException("Invalid file");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        
        // Extract file extension
        if (originalFilename.lastIndexOf(".") > 0) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generate unique filename preserving original extension
        String publicId = "attachments/" + System.currentTimeMillis() + "_" + 
                         originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Upload as "raw" resource type (not image/video)
        // Use original filename to preserve file extension and format
        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(), 
            ObjectUtils.asMap(
                "resource_type", "raw",
                "public_id", publicId,
                "use_filename", true,
                "unique_filename", false,
                "overwrite", false,
                "format", fileExtension.replace(".", "") // Preserve original format
            )
        );

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Upload attachment with custom public ID
     * 
     * @param file MultipartFile to upload
     * @param publicId Custom public ID for the file
     * @return URL of uploaded file on Cloudinary
     * @throws IOException if upload fails
     */
    public String uploadAttachmentWithPublicId(MultipartFile file, String publicId) throws IOException {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IOException("Invalid file");
        }

        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(), 
            ObjectUtils.asMap(
                "resource_type", "raw",
                "folder", "attachments",
                "public_id", publicId
            )
        );

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Delete attachment from Cloudinary
     * 
     * @param publicId Public ID of the file to delete
     * @return true if deleted successfully
     * @throws IOException if deletion fails
     */
    public boolean deleteAttachment(String publicId) throws IOException {
        Map result = cloudinary.uploader().destroy(
            publicId,
            ObjectUtils.asMap("resource_type", "raw")
        );
        
        return "ok".equals(result.get("result"));
    }
}
