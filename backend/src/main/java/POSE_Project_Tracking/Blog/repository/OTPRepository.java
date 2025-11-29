package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {


    Optional<OTP> findByUserId(Long userId);
}
