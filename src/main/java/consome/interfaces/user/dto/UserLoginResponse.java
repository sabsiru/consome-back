package consome.interfaces.user.dto;

import consome.domain.user.Role;
import consome.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private Long id;
    private String loginId;
    private String nickName;
    private Role role;

    public static UserLoginResponse from(User user) {
        return new UserLoginResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getRole()
        );
    }
}
