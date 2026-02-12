package consome.interfaces.user.dto;

import consome.application.user.UserNicknameChangeResult;

public record NicknameChangeResponse(
        String nickname,
        int remainingPoint
) {
    public static NicknameChangeResponse from(UserNicknameChangeResult result) {
        return new NicknameChangeResponse(result.nickname(), result.remainingPoint());
    }
}
