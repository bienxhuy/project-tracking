//package POSE_Project_Tracking.Blog.config;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class DotenvConfig {
//
//    @PostConstruct
//    public void loadDotenv() {
//        Dotenv dotenv = Dotenv.configure()
//                              .ignoreIfMissing()
//                              .load();
//
//        setIfPresent("MAIL_USERNAME", dotenv);
//        setIfPresent("MAIL_PASSWORD", dotenv);
//        setIfPresent("FRONT_END_URL", dotenv);
//        setIfPresent("SECRET_KEY", dotenv);
//        setIfPresent("GOOGLE_CLIENT_SECRET", dotenv);
//        setIfPresent("BACK_END_URL", dotenv);
//        setIfPresent("DATA_SOURCE_URL", dotenv);
//        setIfPresent("DB_USERNAME", dotenv);
//        setIfPresent("DB_PASSWORD", dotenv);
//    }
//
//    private void setIfPresent(String key, Dotenv dotenv) {
//        String value = dotenv.get(key);
//        if (value != null) {
//            System.setProperty(key, value);
//        }
//    }
//}