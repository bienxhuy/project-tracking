package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Milestone;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.enums.EMilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByProject(Project project);

    List<Milestone> findByProjectId(Long projectId);

    List<Milestone> findByStatus(EMilestoneStatus status);

    List<Milestone> findByProjectIdAndStatus(Long projectId, EMilestoneStatus status);

    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId ORDER BY m.orderNumber ASC")
    List<Milestone> findByProjectIdOrderByOrderNumber(@Param("projectId") Long projectId);

    @Query("SELECT m FROM Milestone m WHERE m.endDate < :date AND m.status != 'COMPLETED'")
    List<Milestone> findOverdueMilestones(@Param("date") LocalDate date);

    @Query("SELECT m FROM Milestone m LEFT JOIN FETCH m.reports WHERE m.id = :id")
    Optional<Milestone> findByIdWithReports(@Param("id") Long id);

    @Query("SELECT m FROM Milestone m JOIN FETCH m.project WHERE m.id = :id")
    Optional<Milestone> findByIdWithProject(@Param("id") Long id);

    @Query("SELECT m FROM Milestone m LEFT JOIN FETCH m.tasks WHERE m.id = :id")
    Optional<Milestone> findByIdWithTasks(@Param("id") Long id);

    @Query("SELECT DISTINCT m FROM Milestone m LEFT JOIN FETCH m.tasks WHERE m.project.id = :projectId ORDER BY m.orderNumber ASC")
    List<Milestone> findByProjectIdWithTasks(@Param("projectId") Long projectId);
}
