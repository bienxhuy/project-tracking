package OOSE_Final_Project.Blog.constant;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Router {

    @Value("${FRONT_END_URL}")
    private String frontEndUrl;

    private static String FRONT_END_URL;

    @PostConstruct
    public void init() {
        FRONT_END_URL = frontEndUrl;
    }

    public static String getComicBlog(long blogId) {
        return "/comic/" + blogId;
    }

    public static String getCharacterBlog(long blogId) {
        return "/character/" + blogId;
    }
}
