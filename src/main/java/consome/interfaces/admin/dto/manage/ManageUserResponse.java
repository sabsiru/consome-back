package consome.interfaces.admin.dto.manage;

import consome.application.admin.UserRowResult;
import consome.domain.admin.ManagedBoardInfo;
import consome.domain.user.Role;

import java.util.List;

public record ManageUserResponse(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint,
        int level,
        List<ManagedBoardInfo> managedBoards
) {
    public static ManageUserResponse from(UserRowResult result) {
        return new ManageUserResponse(
                result.userId(),
                result.loginId(),
                result.nickname(),
                result.role(),
                result.userPoint(),
                result.level(),
                result.managedBoards()
        );
    }
}
