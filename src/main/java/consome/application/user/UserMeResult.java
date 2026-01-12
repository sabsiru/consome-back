package consome.application.user;

import consome.domain.user.Role;

public record UserMeResult(
        Long userId,
        String loginId,
        String nickname,
        int point,
        Role role
) {}