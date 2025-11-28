package POSE_Project_Tracking.Blog.util;

import POSE_Project_Tracking.Blog.config.UploadPathHolder;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class FileUtil {


    public static boolean isImageFile(MultipartFile file) throws FileUploadException {

        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "webp");
        final String fileName = file.getOriginalFilename();

        // Check file empty
        if (file.isEmpty()) {
            throw new FileUploadException("You have not selected any files, please select a file");
        }

        boolean isValidExtension = allowedExtensions.stream()
                                                    .anyMatch(ext -> fileName.toLowerCase()
                                                                             .endsWith("." + ext));
        if (!isValidExtension) {
            throw new IllegalArgumentException("Invalid file format");
        }
        return true;
    }

    public static String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID() + "_" + filename;

        // Dùng đường tuyệt đối từ cấu hình (đã set bằng ENV)
        Path uploadPath = Paths.get(UploadPathHolder.uploadDir)
                               .toAbsolutePath()
                               .normalize();

        // Tạo thư mục nếu chưa tồn tại
        Files.createDirectories(uploadPath);

        // Tạo file đích
        Path targetPath = uploadPath.resolve(uniqueFilename);
        System.out.println(">>> Check path: " + targetPath);
        // Lưu file vào disk
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFilename;
    }

    /**
     * Store attachment file (PDF, DOC, DOCX, ZIP, etc.)
     * This method doesn't validate file type - validation should be done before calling
     */
    public static String storeAttachmentFile(MultipartFile file) throws IOException {
        if (file.getOriginalFilename() == null || file.isEmpty()) {
            throw new IOException("Invalid file");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Add UUID to ensure unique filename
        String uniqueFilename = UUID.randomUUID() + "_" + filename;

        // Use absolute path from configuration
        Path uploadPath = Paths.get(UploadPathHolder.uploadDir)
                               .toAbsolutePath()
                               .normalize();

        // Create directory if not exists
        Files.createDirectories(uploadPath);

        // Create target file
        Path targetPath = uploadPath.resolve(uniqueFilename);
        System.out.println(">>> Check attachment upload path: " + targetPath);
        
        // Save file to disk
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFilename;
    }

    public static String storePreviewFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID() + "_" + filename;

        String absPath = new File(UploadPathHolder.previewDir).getAbsolutePath();
        File folder = new File(absPath);
        if (!folder.exists()) {
            folder.mkdirs(); // Tạo thư mục nếu chưa có
        }

        File dest = new File(folder, uniqueFilename);
        file.transferTo(dest);
        return uniqueFilename;
    }

    public static boolean deleteFile(String fileName) {
        File folder = new File(UploadPathHolder.uploadDir); // hoặc dùng biến đã cấu hình từ @Value
        File file = new File(folder, fileName);

        if (file.exists()) {
            return file.delete(); // Trả về true nếu xóa thành công
        } else {
            return false; // File không tồn tại
        }
    }

}
