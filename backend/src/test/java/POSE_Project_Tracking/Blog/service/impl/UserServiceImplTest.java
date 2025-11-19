package POSE_Project_Tracking.Blog.service.impl;

import java.util.Optional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.req.UserUpdateReq;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.User;
import POSE_Project_Tracking.Blog.enums.EUserRole;
import POSE_Project_Tracking.Blog.enums.EUserStatus;
import POSE_Project_Tracking.Blog.mapper.UserMapper;
import POSE_Project_Tracking.Blog.repository.UserRepository;
import POSE_Project_Tracking.Blog.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ImageUploadService imageUploadService;

    @InjectMocks
    private UserServiceImpl service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(EUserRole.STUDENT);
        user.setAccountStatus(EUserStatus.ACTIVE);
        
        // Mock mapper to set fields from request
        lenient().doAnswer(invocation -> {
            UserReq req = invocation.getArgument(0);
            User targetUser = invocation.getArgument(1);
            targetUser.setUsername(req.getUsername());
            targetUser.setEmail(req.getEmail());
            return null;
        }).when(userMapper).transformToEntityFromRequest(any(UserReq.class), any(User.class));
    }

    @Test
    void createUser_validRequest_createsUser() {
        UserReq req = UserReq.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .displayName("New User")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRes result = service.createUser(req);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(userMapper).transformToEntityFromRequest(any(UserReq.class), any(User.class));
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        UserReq req = UserReq.builder()
                .username("testuser")
                .email("new@example.com")
                .password("password")
                .displayName("Test")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.createUser(req));
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        UserReq req = UserReq.builder()
                .username("newuser")
                .email("test@example.com")
                .password("password")
                .displayName("Test")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> service.createUser(req));
    }

    @Test
    void getUserById_existingUser_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserRes result = service.getUserById(1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_nonExistingUser_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getUserById(999L));
    }

    @Test
    void updateUser_validRequest_updatesUser() {
        UserUpdateReq req = new UserUpdateReq();
        req.setUsername("updateduser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRes result = service.updateUser(1L, req);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_duplicateUsername_throwsException() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("existinguser");

        UserUpdateReq req = new UserUpdateReq();
        req.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(otherUser));

        assertThrows(IllegalArgumentException.class, () -> service.updateUser(1L, req));
    }

    @Test
    void updateUserStatus_validRequest_updatesStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRes result = service.updateUserStatus(1L, EUserStatus.INACTIVE);

        assertNotNull(result);
        assertEquals(EUserStatus.INACTIVE, user.getAccountStatus());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_existingUser_deletesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        service.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_nonExistingUser_throwsException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.deleteUser(999L));
    }

    @Test
    void findByUsernameOrEmail_existingUser_returnsUser() {
        when(userRepository.findByUsernameOrEmail("testuser")).thenReturn(Optional.of(user));

        User result = service.findByUsernameOrEmail("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findByUsernameOrEmail_nonExistingUser_throwsException() {
        when(userRepository.findByUsernameOrEmail("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.findByUsernameOrEmail("unknown"));
    }
}

