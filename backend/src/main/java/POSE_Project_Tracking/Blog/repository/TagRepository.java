package POSE_Project_Tracking.Blog.repository;

import POSE_Project_Tracking.Blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {


    Optional<Tag> findByName(String name);
}
