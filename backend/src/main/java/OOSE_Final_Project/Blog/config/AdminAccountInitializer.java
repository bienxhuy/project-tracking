package OOSE_Final_Project.Blog.config;

import OOSE_Final_Project.Blog.entity.User;
import OOSE_Final_Project.Blog.enums.ELoginType;
import OOSE_Final_Project.Blog.enums.ERole;
import OOSE_Final_Project.Blog.enums.EUserStatus;
import OOSE_Final_Project.Blog.repository.UserRepository;
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
            admin.setRole(ERole.ADMIN); // enum hoặc String, tùy bạn
            admin.setDisplayName("Admin");
            admin.setAccountStatus(EUserStatus.ACTIVE);
            admin.setLoginType(ELoginType.LOCAL);
            userRepository.save(admin);
            System.out.println("✅ Admin account created.");
        } else {
            System.out.println("ℹ️ Admin account already exists.");
        }
    }
}

