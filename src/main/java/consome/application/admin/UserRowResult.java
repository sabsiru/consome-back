package consome.application.admin;

import consome.domain.user.Role;

public record UserRowResult(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint
) {
}
