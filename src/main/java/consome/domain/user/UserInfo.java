package consome.domain.user;

public record UserInfo(
        Long userId,
        String username,
        String nickname,
        Role role,
        int point
) {
}
