package consome.application.auth;

public record TokenRefreshResult(
        String accessToken,
        String refreshToken
) {}
