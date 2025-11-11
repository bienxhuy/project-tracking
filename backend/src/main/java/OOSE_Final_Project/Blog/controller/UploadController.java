package OOSE_Final_Project.Blog.controller;

import OOSE_Final_Project.Blog.dto.res.ApiResponse;
import OOSE_Final_Project.Blog.service.impl.ImageUploadService;
import OOSE_Final_Project.Blog.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Value("${BACK_END_URL}")
    private String BASE_URL;

    @Value("${project.upload-file.base-uri}")
    private String upload_base_uri;

    @Value("${project.upload-file.base-preview-uri}")
    private String upload_base_preview_uri;

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> uploadMultipleImages(
            @RequestParam("images") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String url = imageUploadService.uploadImage(file);
                urls.add(url);
            }
            Map<String, List<String>> response = new HashMap<>();
            response.put("urls", urls);
            return ResponseEntity.ok()
                                 .body(new ApiResponse<>(HttpStatus.CREATED, "", response, null));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not upload files");
        }
    }


    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> uploadMultipleImagesPreview(
            @RequestParam("images") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                urls.add(BASE_URL + "/" + upload_base_preview_uri + FileUtil.storePreviewFile(file));
            }
            Map<String, List<String>> response = new HashMap<>();
            response.put("urls", urls);
            return ResponseEntity.ok()
                                 .body(new ApiResponse<>(HttpStatus.CREATED, "", response, null));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not upload files");
        }
    }
}
