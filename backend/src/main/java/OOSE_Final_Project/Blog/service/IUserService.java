package OOSE_Final_Project.Blog.service;


import OOSE_Final_Project.Blog.dto.req.UserReq;
import OOSE_Final_Project.Blog.dto.req.UserUpdateReq;
import OOSE_Final_Project.Blog.dto.res.user.UserRes;
import OOSE_Final_Project.Blog.entity.User;
import OOSE_Final_Project.Blog.enums.ERole;
import OOSE_Final_Project.Blog.enums.EUserStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {

    UserRes createUser(UserReq user);

    List<UserRes> getAllUsers();

    UserRes getUserById(Long id);

    UserRes getUserByUsername(String username);

    UserRes getUserByEmail(String email);

    UserRes updateUser(Long id, UserUpdateReq userDetails);

    void deleteUser(Long id);

    UserRes updateUserStatus(Long id, EUserStatus status);

    UserRes updateUserAvatar(Long id, MultipartFile avatar) throws IOException;

    User findByUsernameOrEmail(String id);

    List<UserRes> findUsersByIds(List<Long> ids);

    String updateUserToken(Long id, UserUpdateReq userDetails);

    Long getCountOfUsers(ERole role);
}
