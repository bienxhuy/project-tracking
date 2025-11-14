package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByInstructor(User instructor);

    List<Project> findByStatus(EProjectStatus status);

    List<Project> findByYearAndSemester(Integer year, Integer semester);

    List<Project> findByFaculty(String faculty);

    @Query("SELECT p FROM Project p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Project> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.milestones WHERE p.id = :id")
    Optional<Project> findByIdWithMilestones(@Param("id") Long id);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id")
    Optional<Project> findByIdWithTasks(@Param("id") Long id);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.id = :id")
    Optional<Project> findByIdWithMembers(@Param("id") Long id);

    @Query("SELECT p FROM Project p WHERE p.instructor.id = :instructorId AND p.status = :status")
    List<Project> findByInstructorIdAndStatus(@Param("instructorId") Long instructorId, @Param("status") EProjectStatus status);
}
