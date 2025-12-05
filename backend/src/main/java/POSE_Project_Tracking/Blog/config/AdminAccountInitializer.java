package POSE_Project_Tracking.Blog.config;

import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminAccountInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";
        String adminEmail = "admin@example.com";

        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu nên mạnh hơn
            admin.setRole(EUserRole.ADMIN); // enum hoặc String, tùy bạn
            admin.setDisplayName("Admin");
            userRepository.save(admin);
            System.out.println("✅ Admin account created.");
        } else {
            System.out.println("ℹ️ Admin account already exists.");
        }
    }
}

