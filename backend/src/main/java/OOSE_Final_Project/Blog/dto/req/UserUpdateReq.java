package OOSE_Final_Project.Blog.dto.req;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateReq {

    @Pattern(regexp = "^\\s*\\S.*$", message = "Không được chỉ chứa khoảng trắng nếu có giá trị")
    private String username;

    @Pattern(regexp = "^\\s*\\S.*$", message = "Không được chỉ chứa khoảng trắng nếu có giá trị")
    private String password;

    @Pattern(regexp = "^\\s*\\S.*$", message = "Không được chỉ chứa khoảng trắng nếu có giá trị")
    private String displayName;

}
