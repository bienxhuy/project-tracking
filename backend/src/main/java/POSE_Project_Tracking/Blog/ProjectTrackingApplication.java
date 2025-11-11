package POSE_Project_Tracking.Blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.nio.charset.Charset;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan("POSE_Project_Tracking.Blog.entity")
public class ProjectTrackingApplication {

    public static void main(String[] args) {


        SpringApplication.run(ProjectTrackingApplication.class, args);
        System.out.println("Working dir (user.dir) = " + System.getProperty("user.dir"));
        System.out.println("File encoding  = " + Charset.defaultCharset().displayName());
        System.out.println("Jenkins applied successfully");

    }

}
