package consome.interfaces.user.dto;

import consome.domain.user.Role;

import java.util.List;

public record UserMeResponse(
        Long userId,
        String loginId,
        String nickname,
        int point,
        Role role,
        List<Long> managedBoardIds
) {}