package consome.interfaces.user.dto;

public record UserMeResponse(
        String loginId,
        String nickname,
        int point
) {}