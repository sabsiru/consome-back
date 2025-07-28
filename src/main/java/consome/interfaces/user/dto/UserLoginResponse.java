package consome.interfaces.user.dto;

import consome.application.user.UserLoginResult;
import consome.domain.user.Role;

public record UserLoginResponse(
        Long id,
        String loginId,
        String nickname,
        Role role,
        int point
) {
    public static UserLoginResponse from(UserLoginResult result) {
        return new UserLoginResponse(
                result.id(),
                result.loginId(),
                result.nickname(),
                result.role(),
                result.point()
        );
    }
}

