package consome.application.user;

import consome.domain.user.Role;

public record UserMeResult(
        String loginId,
        String nickname,
        int point,
        Role role
) {}