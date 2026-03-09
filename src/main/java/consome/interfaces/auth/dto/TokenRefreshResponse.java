package consome.interfaces.auth.dto;

import consome.application.auth.TokenRefreshResult;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenRefreshResponse from(TokenRefreshResult result) {
        return new TokenRefreshResponse(result.accessToken(), result.refreshToken());
    }
}
