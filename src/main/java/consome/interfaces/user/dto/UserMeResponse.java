package consome.interfaces.user.dto;

import consome.domain.user.Role;

public record UserMeResponse(
        String loginId,
        String nickname,
        int point,
        Role role
) {}