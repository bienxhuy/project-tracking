package OOSE_Final_Project.Blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadPathHolder {

    public static String uploadDir;

    public static String previewDir;


    @Value("${project.upload-file.base-uri}")
    public void setUploadDir(String path) {
        UploadPathHolder.uploadDir = path;
    }

    @Value("${project.upload-file.base-preview-uri}")
    public void setPreviewDir(String path) {
        UploadPathHolder.previewDir = path;
    }


}