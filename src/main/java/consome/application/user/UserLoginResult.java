package consome.application.user;

import consome.domain.user.Role;

import java.util.List;

public record UserLoginResult(
        Long userId,
        String loginId,
        String nickname,
        Role role,
        int point,
        int level,
        String accessToken,
        List<Long> managedBoardIds
){}

