package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.dto.req.LoginReq;
import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.res.ApiResponse;
import POSE_Project_Tracking.Blog.dto.res.LoginRes;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.RefreshToken;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.enums.ErrorCode;
import POSE_Project_Tracking.Blog.facade.UserOTPFacade;
import POSE_Project_Tracking.Blog.repository.RefreshTokenRepository;
import POSE_Project_Tracking.Blog.service.IUserService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private IUserService userService;

    @Value("${jwt.refresh-token-valid-in-seconds}")
    private long refreshTokenExpiration;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserOTPFacade userOTPFacade;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginRes>> login(@Valid @RequestBody LoginReq loginReq) {

        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginReq.getIdentifier(), loginReq.getPassword());

        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                                                                    .authenticate(authenticationToken);

        SecurityContextHolder.getContext()
                             .setAuthentication(authentication);

        var currentUser = userService.findByUsernameOrEmail(loginReq.getIdentifier());
        
        // Auto-activate on first login
        if (currentUser.getAccountStatus().equals(EUserStatus.VERIFYING)) {
            userService.updateUserStatus(currentUser.getId(), EUserStatus.ACTIVE);
            currentUser = userService.findByUsernameOrEmail(loginReq.getIdentifier());
        }
        
        // Block inactive users
        if (currentUser.getAccountStatus().equals(EUserStatus.INACTIVE)) {
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(new ApiResponse<>(
                                         ErrorCode.UNAUTHORIZED.getHttpStatus(),
                                         "Tài khoản đã bị vô hiệu hóa",
                                         new LoginRes(null, currentUser.getId()),
                                         "ACCOUNT_INACTIVE"));
        }

        String accessToken = securityUtil.createAccessToken(currentUser);
        LoginRes loginRes = new LoginRes();

        loginRes.setAccessToken(accessToken);

        String refreshToken = securityUtil.createRefreshToken(currentUser);

        refreshTokenRepository.findByUserId(currentUser.getId())
                              .ifPresent(oldRefreshToken -> refreshTokenRepository.deleteById(oldRefreshToken.getId()));

        refreshTokenRepository.save(new RefreshToken(currentUser, refreshToken));

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
                                                      .httpOnly(true)
                                                      .path("/")
                                                      .secure(true)
                                                      .maxAge(refreshTokenExpiration)
                                                      .build();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                             .body(new ApiResponse<>(
                                     HttpStatus.CREATED, "Loggin successfully", loginRes,
                                     null));
    }


    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginRes>> getRefreshToken(
            @CookieValue(name = "refresh_token") String refreshToken
    ) {
        var decodedToken = securityUtil.checkValidRefreshToken(refreshToken);

        String username = decodedToken.getSubject();

        var currentUser = userService.findByUsernameOrEmail(username);

        RefreshToken oldRefreshToken =
                refreshTokenRepository.findByUserId(currentUser.getId())
                                      .orElseThrow(() -> new IllegalArgumentException("Refresh token does not exist " +
                                                                                              "in database"));
        if (!oldRefreshToken.getRefreshToken()
                            .equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token does not match");
        }
        refreshTokenRepository.deleteById(oldRefreshToken.getId());
        String newRefreshToken = securityUtil.createRefreshToken(currentUser);
        refreshTokenRepository.save(new RefreshToken(currentUser, newRefreshToken));

        User user = userService.findByUsernameOrEmail(username);

        String accessToken = securityUtil.createAccessToken(user);

        LoginRes loginRes = new LoginRes();

        loginRes.setAccessToken(accessToken);

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                                                      .httpOnly(true)
                                                      .path("/")
                                                      .secure(true)
                                                      .maxAge(refreshTokenExpiration)
                                                      .build();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                             .body(new ApiResponse<>(
                                     HttpStatus.CREATED, "Get access token successfully", loginRes,
                                     null));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout() {
        String username = SecurityUtil.getCurrentUserLogin()
                                      .isPresent() ? SecurityUtil.getCurrentUserLogin()
                                                                 .get() : null;

        User user = userService.findByUsernameOrEmail(username);
        refreshTokenRepository.deleteByUserId(user.getId());
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", null)
                                                      .httpOnly(true)
                                                      .path("/")
                                                      .secure(true)
                                                      .maxAge(0)
                                                      .build();
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                             .body(new ApiResponse<>(
                                     HttpStatus.OK, "Log out", Boolean.TRUE,
                                     null));
    }


    @GetMapping("/account")
    public ResponseEntity<ApiResponse<UserRes>> fetchAccount() {
        String username = SecurityUtil.getCurrentUserLogin()
                                      .isPresent() ? SecurityUtil.getCurrentUserLogin()
                                                                 .get() : null;

        var user = userService.getUserByUsername(username);
        return ResponseEntity.ok()
                             .body(new ApiResponse<>(
                                     HttpStatus.CREATED, "Fetch user successfully", user,
                                     null));
    }


    @PostMapping("/register")
    public ApiResponse<UserRes> registerUser(@Valid @RequestBody UserReq user) {
        UserRes created = userOTPFacade.createUser(user);
        return new ApiResponse<>(HttpStatus.CREATED, "User created successfully", created, null);
    }

}
