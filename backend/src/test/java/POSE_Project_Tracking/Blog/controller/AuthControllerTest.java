package POSE_Project_Tracking.Blog.controller;

import POSE_Project_Tracking.Blog.config.FirebaseConfig;
import POSE_Project_Tracking.Blog.config.TestConfig;
import POSE_Project_Tracking.Blog.dto.req.LoginReq;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.RefreshToken;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.enums.ErrorCode;
import POSE_Project_Tracking.Blog.repository.RefreshTokenRepository;
import POSE_Project_Tracking.Blog.service.IUserService;
import POSE_Project_Tracking.Blog.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho AuthController - Login Flow
 * Test các trường hợp:
 * - Login thành công với user ACTIVE
 * - Login với user đang VERIFYING
 * - Login với credentials không hợp lệ
 * - Refresh token flow
 * - Logout flow
 * 
 * Note: Sử dụng H2 in-memory database cho testing
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private IUserService userService;

    @MockBean
    private SecurityUtil securityUtil;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    private User activeUser;
    private User verifyingUser;
    private User bannedUser;
    private LoginReq validLoginReq;

    @BeforeEach
    void setUp() {
        // Setup Active User
        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setUsername("activeuser");
        activeUser.setEmail("active@example.com");
        activeUser.setPassword("encodedPassword");
        activeUser.setRole(EUserRole.STUDENT);
        activeUser.setDisplayName("Active User");

        // Setup Verifying User
        verifyingUser = new User();
        verifyingUser.setId(2L);
        verifyingUser.setUsername("verifyinguser");
        verifyingUser.setEmail("verifying@example.com");
        verifyingUser.setPassword("encodedPassword");
        verifyingUser.setRole(EUserRole.STUDENT);
        verifyingUser.setDisplayName("Verifying User");

        // Setup Banned User
        bannedUser = new User();
        bannedUser.setId(3L);
        bannedUser.setUsername("banneduser");
        bannedUser.setEmail("banned@example.com");
        bannedUser.setPassword("encodedPassword");
        bannedUser.setRole(EUserRole.STUDENT);
        bannedUser.setDisplayName("Banned User");

        // Setup Login Request
        validLoginReq = new LoginReq();
        validLoginReq.setIdentifier("activeuser");
        validLoginReq.setPassword("password123");

        // Mock AuthenticationManagerBuilder
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
    }

    @Test
    @DisplayName("Test 1: Login thành công với user ACTIVE - trả về access token và set refresh token cookie")
    void testLogin_Success_WithActiveUser() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        when(securityUtil.createAccessToken(activeUser)).thenReturn("mock-access-token");
        when(securityUtil.createRefreshToken(activeUser)).thenReturn("mock-refresh-token");
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(ArgumentMatchers.any(RefreshToken.class))).thenReturn(new RefreshToken());

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Loggin successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Secure")));

        // Verify
        verify(authenticationManager).authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsernameOrEmail("activeuser");
        verify(securityUtil).createAccessToken(activeUser);
        verify(securityUtil).createRefreshToken(activeUser);
        verify(refreshTokenRepository).save(ArgumentMatchers.any(RefreshToken.class));
    }

    @Test
    @DisplayName("Test 2: Login với user đang VERIFYING - auto-activate và trả về success")
    void testLogin_WithVerifyingUser_ReturnsVerifyingError() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(userService.findByUsernameOrEmail("verifyinguser")).thenReturn(verifyingUser);
        
        // Mock UserRes sau khi được activate (updateUserStatus trả về UserRes)
        UserRes activatedUserRes = new UserRes();
        activatedUserRes.setId(2L);
        activatedUserRes.setUsername("verifyinguser");
        activatedUserRes.setEmail("verifying@example.com");
        activatedUserRes.setRole(EUserRole.STUDENT);
        activatedUserRes.setDisplayName("Verifying User");
        
        // Mock user entity sau khi được activate (findByUsernameOrEmail trả về User)
        User activatedUser = new User();
        activatedUser.setId(2L);
        activatedUser.setUsername("verifyinguser");
        activatedUser.setEmail("verifying@example.com");
        activatedUser.setPassword("encodedPassword");
        activatedUser.setRole(EUserRole.STUDENT);
        activatedUser.setDisplayName("Verifying User");
        
        // findByUsernameOrEmail trả về User - lần đầu VERIFYING, lần sau ACTIVE
        when(userService.findByUsernameOrEmail("verifyinguser"))
                .thenReturn(verifyingUser)  // Lần đầu trả về VERIFYING
                .thenReturn(activatedUser);  // Lần sau trả về ACTIVE
        
        when(securityUtil.createAccessToken(activatedUser)).thenReturn("mock-access-token");
        when(securityUtil.createRefreshToken(activatedUser)).thenReturn("mock-refresh-token");
        when(refreshTokenRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(ArgumentMatchers.any(RefreshToken.class))).thenReturn(new RefreshToken());

        LoginReq verifyingLoginReq = new LoginReq();
        verifyingLoginReq.setIdentifier("verifyinguser");
        verifyingLoginReq.setPassword("password123");

        // Act & Assert - Bây giờ expect success vì user được auto-activate
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyingLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Loggin successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));

        // Verify - user được activate và tạo token
        verify(securityUtil).createAccessToken(activatedUser);
        verify(securityUtil).createRefreshToken(activatedUser);
    }

    @Test
    @DisplayName("Test 4: Login với sai password - authentication fail")
    void testLogin_WithInvalidPassword_ThrowsBadCredentials() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginReq invalidLoginReq = new LoginReq();
        invalidLoginReq.setIdentifier("activeuser");
        invalidLoginReq.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginReq)))
                .andExpect(status().isUnauthorized());

        // Verify - không gọi các service khác
        verify(userService, never()).findByUsernameOrEmail(anyString());
        verify(securityUtil, never()).createAccessToken(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("Test 5: Login với username hoặc email - cả hai đều hoạt động")
    void testLogin_WithUsernameOrEmail_BothWork() throws Exception {
        // Test với username
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        when(userService.findByUsernameOrEmail("active@example.com")).thenReturn(activeUser);
        when(securityUtil.createAccessToken(activeUser)).thenReturn("mock-access-token");
        when(securityUtil.createRefreshToken(activeUser)).thenReturn("mock-refresh-token");
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Test với username
        LoginReq usernameLogin = new LoginReq();
        usernameLogin.setIdentifier("activeuser");
        usernameLogin.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));

        // Test với email
        LoginReq emailLogin = new LoginReq();
        emailLogin.setIdentifier("active@example.com");
        emailLogin.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));
    }

    @Test
    @DisplayName("Test 6: Login thay thế refresh token cũ nếu đã tồn tại")
    void testLogin_ReplacesOldRefreshToken_WhenExists() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        when(securityUtil.createAccessToken(activeUser)).thenReturn("mock-access-token");
        when(securityUtil.createRefreshToken(activeUser)).thenReturn("new-refresh-token");

        // Mock old refresh token exists
        RefreshToken oldRefreshToken = new RefreshToken();
        oldRefreshToken.setId(100L);
        oldRefreshToken.setUser(activeUser);
        oldRefreshToken.setRefreshToken("old-refresh-token");
        
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of(oldRefreshToken));

        // Act
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginReq)))
                .andExpect(status().isOk());

        // Assert - xóa refresh token cũ và lưu token mới
        verify(refreshTokenRepository).deleteById(100L);
        verify(refreshTokenRepository).save(ArgumentMatchers.any(RefreshToken.class));
    }

    @Test
    @DisplayName("Test 7: Refresh token thành công - trả về access token mới")
    void testRefreshToken_Success_ReturnsNewAccessToken() throws Exception {
        // Arrange
        String refreshToken = "valid-refresh-token";
        Jwt jwt = mock(Jwt.class);
        
        when(jwt.getSubject()).thenReturn("activeuser");
        when(securityUtil.checkValidRefreshToken(refreshToken)).thenReturn(jwt);
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        
        RefreshToken storedRefreshToken = new RefreshToken();
        storedRefreshToken.setId(1L);
        storedRefreshToken.setUser(activeUser);
        storedRefreshToken.setRefreshToken(refreshToken);
        
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of(storedRefreshToken));
        when(securityUtil.createAccessToken(activeUser)).thenReturn("new-access-token");
        when(securityUtil.createRefreshToken(activeUser)).thenReturn("new-refresh-token");

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/refresh")
                .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Get access token successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("new-refresh-token")));

        // Verify - xóa refresh token cũ và tạo mới
        verify(refreshTokenRepository).deleteById(1L);
        verify(refreshTokenRepository).save(ArgumentMatchers.any(RefreshToken.class));
        verify(securityUtil).createAccessToken(activeUser);
        verify(securityUtil).createRefreshToken(activeUser);
    }

    @Test
    @DisplayName("Test 8: Refresh token không hợp lệ - throw exception")
    void testRefreshToken_WithInvalidToken_ThrowsException() throws Exception {
        // Arrange
        String invalidRefreshToken = "invalid-refresh-token";
        
        when(securityUtil.checkValidRefreshToken(invalidRefreshToken))
                .thenThrow(new IllegalArgumentException("Invalid refresh token"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/refresh")
                .cookie(new Cookie("refresh_token", invalidRefreshToken)))
                .andExpect(status().is4xxClientError());

        // Verify
        verify(securityUtil).checkValidRefreshToken(invalidRefreshToken);
        verify(refreshTokenRepository, never()).findByUserId(anyLong());
    }

    @Test
    @WithMockUser(username = "activeuser")
    @DisplayName("Test 11: Logout thành công - xóa refresh token và clear cookie")
    void testLogout_Success_DeletesTokenAndClearsCookie() throws Exception {
        // Arrange
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        doNothing().when(refreshTokenRepository).deleteByUserId(1L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Log out"))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh_token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        // Verify - xóa refresh token từ database
        verify(refreshTokenRepository).deleteByUserId(1L);
    }

    @Test
    @DisplayName("Test 12: Validation - Login request không có password")
    void testLogin_WithoutPassword_ValidationFails() throws Exception {
        // Arrange
        LoginReq invalidReq = new LoginReq();
        invalidReq.setIdentifier("activeuser");
        // password = null

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());

        // Verify - không authenticate
        verify(authenticationManager, never()).authenticate(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Test 13: Refresh token cookie attributes - HttpOnly, Secure, Path")
    void testLogin_RefreshTokenCookie_HasCorrectAttributes() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        when(securityUtil.createAccessToken(activeUser)).thenReturn("mock-access-token");
        when(securityUtil.createRefreshToken(activeUser)).thenReturn("mock-refresh-token");
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginReq)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert cookie attributes
        String setCookieHeader = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("refresh_token=mock-refresh-token"));
        assertTrue(setCookieHeader.contains("HttpOnly"));
        assertTrue(setCookieHeader.contains("Secure"));
        assertTrue(setCookieHeader.contains("Path=/"));
        assertTrue(setCookieHeader.contains("Max-Age="));
    }

    @Test
    @DisplayName("Test 14: Login với identifier rỗng - validation fails")
    void testLogin_WithEmptyIdentifier_ValidationFails() throws Exception {
        // Arrange
        LoginReq emptyIdentifierReq = new LoginReq();
        emptyIdentifierReq.setIdentifier("");
        emptyIdentifierReq.setPassword("password123");

        // Act & Assert
        // Note: Validation có thể pass ở controller level nhưng fail ở authentication level
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Empty identifier"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyIdentifierReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test 15: Multiple logins - mỗi lần login tạo refresh token mới")
    void testMultipleLogins_CreateNewRefreshTokenEachTime() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(userService.findByUsernameOrEmail("activeuser")).thenReturn(activeUser);
        when(securityUtil.createAccessToken(activeUser)).thenReturn("access-token-1", "access-token-2");
        when(securityUtil.createRefreshToken(activeUser)).thenReturn("refresh-token-1", "refresh-token-2");
        
        // First login - no existing token
        when(refreshTokenRepository.findByUserId(1L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new RefreshToken(activeUser, "refresh-token-1")));

        // First login
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token-1"));

        // Second login
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token-2"));

        // Verify - lưu token 2 lần
        verify(refreshTokenRepository, times(2)).save(ArgumentMatchers.any(RefreshToken.class));
    }
}

