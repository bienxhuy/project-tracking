package OOSE_Final_Project.Blog.repository;

import OOSE_Final_Project.Blog.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {


    Optional<OTP> findByUserId(Long userId);
}
