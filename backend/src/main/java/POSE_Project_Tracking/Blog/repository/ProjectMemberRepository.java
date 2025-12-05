package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.ProjectMember;
import POSE_Project_Tracking.Blog.entity.Project;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProjectId(Long projectId);

    List<ProjectMember> findByUserId(Long userId);

    List<ProjectMember> findByUser(User user);

    List<ProjectMember> findByRole(EUserRole role);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    List<ProjectMember> findByProject(Project project);

    List<ProjectMember> findByProjectIdAndRole(Long projectId, EUserRole role);

    List<ProjectMember> findByUserIdAndIsActive(Long userId, Boolean isActive);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    Optional<ProjectMember> findByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.isActive = true")
    List<ProjectMember> findActiveMembers(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.isActive = true")
    List<ProjectMember> findActiveProjectsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(pm) FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = :role AND pm.isActive = true")
    Long countMembersByProjectAndRole(@Param("projectId") Long projectId, @Param("role") EUserRole role);

    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.user WHERE pm.project.id = :projectId")
    List<ProjectMember> findByProjectIdWithUser(@Param("projectId") Long projectId);

    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    boolean existsByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(pm1) > 0 THEN true ELSE false END FROM ProjectMember pm1 " +
           "WHERE pm1.user.id = :userId1 AND EXISTS (" +
           "SELECT pm2 FROM ProjectMember pm2 WHERE pm2.user.id = :userId2 AND pm2.project.id = pm1.project.id)")
    boolean existsCommonProject(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
