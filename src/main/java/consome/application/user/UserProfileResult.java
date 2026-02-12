package consome.application.user;

import consome.domain.user.Role;

import java.time.LocalDateTime;

public record UserProfileResult(
        Long userId,
        String nickname,
        int level,
        Role role,
        int point,
        int postCount,
        int commentCount,
        LocalDateTime createdAt
) {
}
