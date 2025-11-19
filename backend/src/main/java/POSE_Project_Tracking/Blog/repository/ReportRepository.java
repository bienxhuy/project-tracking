package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByProjectId(Long projectId);

    List<Report> findByMilestoneId(Long milestoneId);

    List<Report> findByTaskId(Long taskId);

    List<Report> findByAuthor(User author);

    List<Report> findByAuthorId(Long authorId);

    List<Report> findByStatus(EReportStatus status);

    List<Report> findByProject(Project project);

    List<Report> findByMilestone(Milestone milestone);

    List<Report> findByTask(Task task);

    List<Report> findByReportDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Report> findByProjectIdAndStatus(Long projectId, EReportStatus status);

    @Query("SELECT r FROM Report r WHERE r.author.id = :authorId AND r.project.id = :projectId")
    List<Report> findByAuthorAndProject(@Param("authorId") Long authorId, @Param("projectId") Long projectId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.comments WHERE r.id = :id")
    Optional<Report> findByIdWithComments(@Param("id") Long id);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.attachments WHERE r.id = :id")
    Optional<Report> findByIdWithAttachments(@Param("id") Long id);
}
