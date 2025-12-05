package POSE_Project_Tracking.Blog.service;


import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.req.UserUpdateReq;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {

    UserRes createUser(UserReq user);

    List<UserRes> getAllUsers();

    List<UserRes> getAllUsers(String search, String role);

    UserRes getUserById(Long id);

    UserRes getUserByUsername(String username);

    UserRes getUserByEmail(String email);

    UserRes updateUser(Long id, UserUpdateReq userDetails);

    void deleteUser(Long id);

    User findByUsernameOrEmail(String id);

    List<UserRes> findUsersByIds(List<Long> ids);

    String updateUserToken(Long id, UserUpdateReq userDetails);

}
