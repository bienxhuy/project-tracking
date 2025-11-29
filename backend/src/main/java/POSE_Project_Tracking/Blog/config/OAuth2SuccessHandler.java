package POSE_Project_Tracking.Blog.config;

import POSE_Project_Tracking.Blog.entity.RefreshToken;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.ELoginType;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.repository.RefreshTokenRepository;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${FRONT_END_URL}")
    private String FRONT_END_URL;

    @Value("${jwt.refresh-token-valid-in-seconds}")
    private long refreshTokenExpiration;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String displayName = oauth2AuthenticationToken.getPrincipal()
                                                      .getAttribute("name");

        String email = oauth2AuthenticationToken.getPrincipal()
                                                .getAttribute("email");

        String avatar = oauth2AuthenticationToken.getPrincipal()
                                                 .getAttribute("picture");

        User user = userRepository.findByUsernameOrEmail(email)
                                  .orElse(null);
        // Đăng ký lần đầu
        if (user == null) {
            user = User.builder()
                       .displayName(displayName)
                       .email(email)
                       .password("")
                       .loginType(ELoginType.GOOGLE)
                       .role(EUserRole.STUDENT)
                       .accountStatus(EUserStatus.ACTIVE)
                       .username(displayName)
                       .build();
            userRepository.save(user);
        }
        
        // Tạo access token và refresh token
        String accessToken = createAccessToken(user);
        String refreshToken = securityUtil.createRefreshToken(user);
        
        // Xóa refresh token cũ nếu có (1 user chỉ có 1 refresh token active)
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(oldRefreshToken -> refreshTokenRepository.deleteById(oldRefreshToken.getId()));
        
        // Lưu refresh token mới vào database
        RefreshToken refreshTokenEntity = new RefreshToken(user, refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        
        // Set refresh token vào HttpOnly cookie để bảo mật
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)      // JavaScript không thể truy cập (chống XSS)
                .secure(true)        // Chỉ gửi qua HTTPS
                .path("/")           // Cookie có hiệu lực toàn site
                .maxAge(refreshTokenExpiration)  // Thời gian sống
                .sameSite("Lax")     // Chống CSRF
                .build();
        
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        
        // Redirect về frontend với access token trong URL
        // LƯU Ý: Vẫn không an toàn 100%, nhưng access token có thời gian ngắn (30 phút)
        // Frontend nên lưu vào memory, không lưu localStorage
        String redirectUrl = FRONT_END_URL + "/oauth2/callback?token=" + accessToken;
        response.sendRedirect(redirectUrl);
    }

    public String createAccessToken(User user) {
        return securityUtil.createAccessToken(user);
    }

}