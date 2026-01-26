package consome.application.user;

import consome.domain.user.Role;

public record UserSearchResult(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint
) {
}
