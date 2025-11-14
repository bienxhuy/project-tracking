package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Comment;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Report;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByProjectId(Long projectId);

    List<Comment> findByMilestoneId(Long milestoneId);

    List<Comment> findByTaskId(Long taskId);

    List<Comment> findByReportId(Long reportId);

    List<Comment> findByAuthor(User author);

    List<Comment> findByAuthorId(Long authorId);

    List<Comment> findByParentCommentId(Long parentCommentId);

    List<Comment> findByProject(Project project);

    List<Comment> findByMilestone(Milestone milestone);

    List<Comment> findByTask(Task task);

    List<Comment> findByReport(Report report);

    List<Comment> findByParentComment(Comment parentComment);

    @Query("SELECT c FROM Comment c WHERE c.parentComment IS NULL AND c.project.id = :projectId")
    List<Comment> findRootCommentsByProject(@Param("projectId") Long projectId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment IS NULL AND c.task.id = :taskId")
    List<Comment> findRootCommentsByTask(@Param("taskId") Long taskId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment IS NULL AND c.report.id = :reportId")
    List<Comment> findRootCommentsByReport(@Param("reportId") Long reportId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.id = :id")
    List<Comment> findByIdWithReplies(@Param("id") Long id);
}
