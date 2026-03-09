package consome.application.auth;

import consome.domain.user.Role;
import consome.domain.user.exception.UserException;
import consome.infrastructure.jwt.JwtProperties;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final TokenRedisRepository tokenRedisRepository;

    public TokenRefreshResult refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UserException.InvalidRefreshToken();
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        Role role = jwtProvider.getRole(refreshToken);

        String storedToken = tokenRedisRepository.findRefreshToken(userId)
                .orElseThrow(UserException.InvalidRefreshToken::new);

        if (!storedToken.equals(refreshToken)) {
            tokenRedisRepository.deleteRefreshToken(userId);
            throw new UserException.InvalidRefreshToken();
        }

        String newAccessToken = jwtProvider.createAccessToken(userId, role);
        String newRefreshToken = jwtProvider.createRefreshToken(userId, role);
        tokenRedisRepository.saveRefreshToken(userId, newRefreshToken, jwtProperties.getRefreshTokenExpiration());

        return new TokenRefreshResult(newAccessToken, newRefreshToken);
    }

    public void logout(Long userId, String accessToken) {
        tokenRedisRepository.deleteRefreshToken(userId);

        long remainingTtl = jwtProvider.getRemainingExpiration(accessToken);
        if (remainingTtl > 0) {
            String jti = jwtProvider.getJti(accessToken);
            tokenRedisRepository.addToBlacklist(jti, remainingTtl);
        }
    }
}
