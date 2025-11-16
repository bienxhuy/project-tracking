package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Attachment;
import POSE_Project_Tracking.Blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByProjectId(Long projectId);

    List<Attachment> findByMilestoneId(Long milestoneId);

    List<Attachment> findByTaskId(Long taskId);

    List<Attachment> findByReportId(Long reportId);

    List<Attachment> findByCommentId(Long commentId);

    List<Attachment> findByUploadedBy(User user);

    List<Attachment> findByUploadedById(Long userId);

    List<Attachment> findByFileType(String fileType);

    @Query("SELECT a FROM Attachment a WHERE a.project.id = :projectId AND a.fileType LIKE %:type%")
    List<Attachment> findByProjectIdAndFileType(@Param("projectId") Long projectId, @Param("type") String type);

    @Query("SELECT SUM(a.fileSize) FROM Attachment a WHERE a.project.id = :projectId")
    Long getTotalFileSizeByProject(@Param("projectId") Long projectId);

    @Query("SELECT SUM(a.fileSize) FROM Attachment a WHERE a.uploadedBy.id = :userId")
    Long getTotalFileSizeByUser(@Param("userId") Long userId);
}
