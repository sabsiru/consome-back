package consome.interfaces.user.dto;

import consome.domain.user.Role;

public record UserMeResponse(
        Long userId,
        String loginId,
        String nickname,
        int point,
        Role role
) {}