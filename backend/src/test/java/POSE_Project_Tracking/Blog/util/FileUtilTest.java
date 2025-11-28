package POSE_Project_Tracking.Blog.util;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileUtilTest {

    @Test
    void isImageFile_validJpgFile_returnsTrue() throws FileUploadException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        boolean result = FileUtil.isImageFile(file);

        assertTrue(result);
    }

    @Test
    void isImageFile_validJpegFile_returnsTrue() throws FileUploadException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpeg");

        boolean result = FileUtil.isImageFile(file);

        assertTrue(result);
    }

    @Test
    void isImageFile_validPngFile_returnsTrue() throws FileUploadException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.png");

        boolean result = FileUtil.isImageFile(file);

        assertTrue(result);
    }

    @Test
    void isImageFile_validWebpFile_returnsTrue() throws FileUploadException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.webp");

        boolean result = FileUtil.isImageFile(file);

        assertTrue(result);
    }

    @Test
    void isImageFile_uppercaseExtension_returnsTrue() throws FileUploadException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.JPG");

        boolean result = FileUtil.isImageFile(file);

        assertTrue(result);
    }

    @Test
    void isImageFile_emptyFile_throwsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            FileUtil.isImageFile(file);
        });

        assertEquals("You have not selected any files, please select a file", exception.getMessage());
    }

    @Test
    void isImageFile_invalidExtension_throwsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.pdf");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            FileUtil.isImageFile(file);
        });

        assertEquals("Invalid file format", exception.getMessage());
    }

    @Test
    void isImageFile_txtFile_throwsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            FileUtil.isImageFile(file);
        });

        assertEquals("Invalid file format", exception.getMessage());
    }

    @Test
    void isImageFile_noExtension_throwsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("testfile");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            FileUtil.isImageFile(file);
        });

        assertEquals("Invalid file format", exception.getMessage());
    }
}

