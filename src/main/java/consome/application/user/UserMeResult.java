package consome.application.user;

import consome.domain.user.Role;

import java.util.List;

public record UserMeResult(
        Long userId,
        String loginId,
        String nickname,
        int point,
        int level,
        Role role,
        List<Long> managedBoardIds
) {}