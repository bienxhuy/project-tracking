package OOSE_Final_Project.Blog.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@UsernameOrEmailRequired
public class LoginReq {

    private String identifier;

    @NotBlank(message = "Password không được để trống")
    private String password;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String username;
    }
}
