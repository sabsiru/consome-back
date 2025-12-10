package consome.interfaces.admin.dto.manage;

import consome.application.admin.UserRowResult;
import consome.domain.user.Role;

public record ManageUserResponse(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint
) {
    public static ManageUserResponse from(UserRowResult result) {
        return new ManageUserResponse(
                result.userId(),
                result.loginId(),
                result.nickname(),
                result.role(),
                result.userPoint()
        );
    }
}
