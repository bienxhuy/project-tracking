package OOSE_Final_Project.Blog.service.impl;

import OOSE_Final_Project.Blog.dto.req.UserReq;
import OOSE_Final_Project.Blog.dto.req.UserUpdateReq;
import OOSE_Final_Project.Blog.dto.res.user.UserRes;
import OOSE_Final_Project.Blog.entity.User;
import OOSE_Final_Project.Blog.enums.ELoginType;
import OOSE_Final_Project.Blog.enums.ERole;
import OOSE_Final_Project.Blog.enums.EUserStatus;
import OOSE_Final_Project.Blog.mapper.UserMapper;
import OOSE_Final_Project.Blog.repository.UserRepository;
import OOSE_Final_Project.Blog.service.IUserService;
import OOSE_Final_Project.Blog.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    ImageUploadService imageUploadService;


    @Override
    public UserRes createUser(UserReq userReq) {
        User user = new User();
        userMapper.transformToEntityFromRequest(userReq, user);
        // Kiểm tra username đã tồn tại
        if (userRepository.findByUsername(user.getUsername())
                .isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }


        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(user.getEmail())
                .isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }


        // Thiết lập giá trị mặc định
        user.setRole(ERole.USER);
        user.setAccountStatus(EUserStatus.VERIFYING);
        user.setLevel(0);
        user.setLoginType(ELoginType.LOCAL);

        user = userRepository.save(user);


        return changeToRes(user);
    }

    @Override
    //    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRes> getAllUsers() {
        var users = userRepository.findAll();
        return users.stream()
                .map(this::changeToRes)
                .collect(Collectors.toList());
    }

    @Override
    public UserRes getUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User Not found"));
        return changeToRes(user);
    }

    @Override
    public UserRes getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User Not found"));
        return changeToRes(user);
    }

    @Override
    public UserRes getUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User Not found"));
        return changeToRes(user);
    }

    @Override
    public UserRes updateUser(Long id, UserUpdateReq userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Cập nhật username nếu không trùng
        if (userDetails.getUsername() != null && !userDetails.getUsername()
                .equals(user.getUsername())) {
            if (userRepository.findByUsername(userDetails.getUsername())
                    .isPresent()) {
                throw new IllegalArgumentException("Username already exists");
            }
        }

        if (userDetails.getPassword() != null && checkNewPassword(userDetails.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New passwords is same as current password");
        }
        userMapper.updateEntityFromRequest(userDetails, user);

        user = userRepository.save(user);
        return changeToRes(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserRes updateUserStatus(Long id, EUserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.setAccountStatus(status);
        user = userRepository.save(user);
        return changeToRes(user);
    }

    @Override
    public UserRes updateUserAvatar(Long id, MultipartFile avatar) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        //        FileUtil.storeFile(avatar);
        String avatarPath = imageUploadService.uploadImage(avatar);
        user.setAvatar(avatarPath);
        user = userRepository.save(user);
        return changeToRes(user);
    }

    @Override
    public User findByUsernameOrEmail(String id) {
        return userRepository.findByUsernameOrEmail(id)
                .orElseThrow(() -> new IllegalArgumentException("User Not found"));
    }

    @Override
    public List<UserRes> findUsersByIds(List<Long> ids) {
        var result = ids.stream()
                .map(id -> userRepository.findById(id)
                        .orElse(null))
                .filter(Objects::nonNull) // bỏ user null
                .map(user -> {
                    UserRes userRes = new UserRes();
                    userMapper.updateUserResponseFromEntity(user, userRes);
                    return userRes;
                })
                .toList();

        return result;
    }

    @Override
    public String updateUserToken(Long id, UserUpdateReq userDetails) {
        var res = this.updateUser(id, userDetails);
        var user = userRepository.findByUsername(res.getUsername())
                .orElse(null);
        return securityUtil.createAccessToken(user);

    }

    @Override
    public Long getCountOfUsers(ERole role) {
        return (long) userRepository.findByRole(role)
                .size();
    }

    //    @Override
    //    public UserRes updateUserPassword(Long id, String newPassword) {
    //        var user = userRepository.findById(id)
    //                                 .orElseThrow(() -> new IllegalArgumentException("User not found with id: " +
    //                                 id));
    //        if (passwordEncoder.matches(newPassword, user.getPassword())) {
    //            throw new IllegalArgumentException("New passwords is same as current password");
    //        }
    //    }


    UserRes changeToRes(User user) {
        UserRes userRes = new UserRes();
        userMapper.updateUserResponseFromEntity(user, userRes);
        return userRes;
    }

    boolean checkNewPassword(String newPassword, String oldPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
