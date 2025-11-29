package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.Task;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.enums.ETaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    // Find tasks where user is in assignedUsers list
    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignedUsers u WHERE u = :user")
    List<Task> findByAssignedUsersContaining(@Param("user") User user);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId")
    List<Task> findByAssignedUsersId(@Param("userId") Long userId);

    List<Task> findByStatus(ETaskStatus status);

    List<Task> findByProjectIdAndStatus(Long projectId, ETaskStatus status);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId AND t.status = :status")
    List<Task> findByAssignedUsersIdAndStatus(@Param("userId") Long userId, @Param("status") ETaskStatus status);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId AND t.status = :status")
    List<Task> findUserTasksByStatus(@Param("userId") Long userId, @Param("status") ETaskStatus status);

    List<Task> findByEndDate(LocalDate endDate);

    @Query("SELECT t FROM Task t WHERE t.endDate < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId AND t.endDate < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasksByUser(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.reports WHERE t.id = :id")
    Optional<Task> findByIdWithReports(@Param("id") Long id);

    @Query("SELECT t FROM Task t JOIN FETCH t.project LEFT JOIN FETCH t.milestone WHERE t.id = :id")
    Optional<Task> findByIdWithProjectAndMilestone(@Param("id") Long id);

    List<Task> findByProject(Project project);

    List<Task> findByMilestone(Milestone milestone);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = 'COMPLETED'")
    Long countCompletedTasksByProject(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.milestone.id = :milestoneId AND t.status = 'COMPLETED'")
    Long countCompletedTasksByMilestone(@Param("milestoneId") Long milestoneId);
}
