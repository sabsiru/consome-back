package consome.application.user;

public record UserLoginCommand(
        String loginId,
        String password
) {
}
