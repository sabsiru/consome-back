package consome.application.user;

public record UserNicknameChangeResult(
        String nickname,
        int remainingPoint
) {
}
