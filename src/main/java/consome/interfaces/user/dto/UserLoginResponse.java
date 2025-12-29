package consome.interfaces.user.dto;

import consome.application.user.UserLoginResult;
import consome.domain.user.Role;

public record UserLoginResponse(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int point,
        String accessToken
) {
    public static UserLoginResponse from(UserLoginResult result) {
        return new UserLoginResponse(
                result.userId(),
                result.loginId(),
                result.nickname(),
                result.role(),
                result.point(),
                result.accessToken()
        );
    }
}

