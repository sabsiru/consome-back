package consome.interfaces.user.dto;

import consome.application.user.UserProfileResult;
import consome.domain.user.Role;

public record UserProfileResponse(
        Long userId,
        String nickname,
        int level,
        Role role,
        int point,
        int postCount,
        int commentCount,
        String createdAt
) {
    public static UserProfileResponse from(UserProfileResult result) {
        return new UserProfileResponse(
                result.userId(),
                result.nickname(),
                result.level(),
                result.role(),
                result.point(),
                result.postCount(),
                result.commentCount(),
                result.createdAt().toLocalDate().toString()
        );
    }
}
