package POSE_Project_Tracking.Blog.mapper;

import POSE_Project_Tracking.Blog.dto.req.UserReq;
import POSE_Project_Tracking.Blog.dto.req.UserUpdateReq;
import POSE_Project_Tracking.Blog.dto.res.user.UserRes;
import POSE_Project_Tracking.Blog.entity.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Mapping(target = "password", source = "password", qualifiedByName = "mapPassword")
    @Mapping(target = "avatar", ignore = true)
    public abstract void transformToEntityFromRequest(UserReq source, @MappingTarget User target);

    @Mapping(target = "password", source = "password", qualifiedByName = "mapPassword")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromRequest(UserUpdateReq source, @MappingTarget User target);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserResponseFromEntity(User source, @MappingTarget UserRes target);

    @Named("mapPassword")
    String mapPassword(String password) {
        return passwordEncoder.encode(password);
    }

}
