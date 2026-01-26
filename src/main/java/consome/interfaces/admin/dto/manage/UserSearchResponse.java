package consome.interfaces.admin.dto.manage;

import consome.application.user.UserSearchResult;
import consome.domain.user.Role;

public record UserSearchResponse(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint
) {
    public static UserSearchResponse from(UserSearchResult result) {
        return new UserSearchResponse(
                result.userId(),
                result.loginId(),
                result.nickname(),
                result.role(),
                result.userPoint()
        );
    }
}
