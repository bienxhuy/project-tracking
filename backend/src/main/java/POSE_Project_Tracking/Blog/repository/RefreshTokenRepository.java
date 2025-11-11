package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long userId);

    @Transactional
    void deleteByUserId(Long userId);

    boolean existsByUserId(Long userId);

}
