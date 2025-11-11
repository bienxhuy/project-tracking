package OOSE_Final_Project.Blog.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackOn = Exception.class)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Lấy thông tin người dùng
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String sub = oAuth2User.getAttribute("sub");

        // TODO: Lưu hoặc cập nhật thông tin người dùng vào database
        // Ví dụ: User user = userRepository.findByEmail(email);
        // Nếu không tồn tại, tạo mới user

        System.out.println(email + " " + name + " " + sub);
        return oAuth2User;
    }
}