package consome.application.user;

public record UserSearchCommand(
        String keyword,
        Long id,
        String loginId,
        String nickname
) {
}
