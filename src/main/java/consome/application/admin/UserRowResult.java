package consome.application.admin;

import consome.domain.admin.ManagedBoardInfo;
import consome.domain.user.Role;

import java.util.List;

public record UserRowResult(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int userPoint,
        int level,
        List<ManagedBoardInfo> managedBoards
) {
}
