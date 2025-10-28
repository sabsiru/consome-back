package consome.application.user;

import consome.domain.user.Role;

public record UserLoginResult(
        Long id,
        String loginId,
        String nickname,
        Role role,
        int point,
        String accessToken
){}

