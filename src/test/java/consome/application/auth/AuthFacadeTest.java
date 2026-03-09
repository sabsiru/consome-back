package consome.application.auth;

import consome.domain.user.Role;
import consome.domain.user.exception.UserException;
import consome.infrastructure.jwt.JwtProperties;
import consome.infrastructure.jwt.JwtProvider;
import consome.infrastructure.redis.TokenRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @InjectMocks
    private AuthFacade authFacade;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenRedisRepository tokenRedisRepository;

    private static final Long USER_ID = 1L;
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final String NEW_REFRESH_TOKEN = "new-refresh-token";

    @Test
    void 유효한_리프레시_토큰으로_토큰_갱신_성공() {
        // given
        when(jwtProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtProvider.getUserId(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtProvider.getRole(REFRESH_TOKEN)).thenReturn(Role.USER);
        when(tokenRedisRepository.findRefreshToken(USER_ID)).thenReturn(Optional.of(REFRESH_TOKEN));
        when(jwtProvider.createAccessToken(USER_ID, Role.USER)).thenReturn(NEW_ACCESS_TOKEN);
        when(jwtProvider.createRefreshToken(USER_ID, Role.USER)).thenReturn(NEW_REFRESH_TOKEN);
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800000L);

        // when
        TokenRefreshResult result = authFacade.refresh(REFRESH_TOKEN);

        // then
        assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
        verify(tokenRedisRepository).saveRefreshToken(eq(USER_ID), eq(NEW_REFRESH_TOKEN), eq(604800000L));
    }

    @Test
    void 만료된_리프레시_토큰이면_예외_발생() {
        // given
        when(jwtProvider.validateToken(REFRESH_TOKEN)).thenReturn(false);

        // then
        assertThatThrownBy(() -> authFacade.refresh(REFRESH_TOKEN))
                .isInstanceOf(UserException.InvalidRefreshToken.class);
    }

    @Test
    void Redis에_저장되지_않은_리프레시_토큰이면_예외_발생() {
        // given
        when(jwtProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtProvider.getUserId(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtProvider.getRole(REFRESH_TOKEN)).thenReturn(Role.USER);
        when(tokenRedisRepository.findRefreshToken(USER_ID)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> authFacade.refresh(REFRESH_TOKEN))
                .isInstanceOf(UserException.InvalidRefreshToken.class);
    }

    @Test
    void 탈취_감지_저장된_토큰과_불일치하면_세션_무효화() {
        // given
        String stolenToken = "stolen-refresh-token";
        when(jwtProvider.validateToken(stolenToken)).thenReturn(true);
        when(jwtProvider.getUserId(stolenToken)).thenReturn(USER_ID);
        when(jwtProvider.getRole(stolenToken)).thenReturn(Role.USER);
        when(tokenRedisRepository.findRefreshToken(USER_ID)).thenReturn(Optional.of(REFRESH_TOKEN));

        // then
        assertThatThrownBy(() -> authFacade.refresh(stolenToken))
                .isInstanceOf(UserException.InvalidRefreshToken.class);
        verify(tokenRedisRepository).deleteRefreshToken(USER_ID);
    }

    @Test
    void 로그아웃_시_리프레시_토큰_삭제_및_액세스_토큰_블랙리스트_등록() {
        // given
        String jti = "test-jti-uuid";
        long remainingTtl = 1800000L;
        when(jwtProvider.getRemainingExpiration(ACCESS_TOKEN)).thenReturn(remainingTtl);
        when(jwtProvider.getJti(ACCESS_TOKEN)).thenReturn(jti);

        // when
        authFacade.logout(USER_ID, ACCESS_TOKEN);

        // then
        verify(tokenRedisRepository).deleteRefreshToken(USER_ID);
        verify(tokenRedisRepository).addToBlacklist(jti, remainingTtl);
    }

    @Test
    void 로그아웃_시_이미_만료된_토큰이면_블랙리스트_등록_안함() {
        // given
        when(jwtProvider.getRemainingExpiration(ACCESS_TOKEN)).thenReturn(0L);

        // when
        authFacade.logout(USER_ID, ACCESS_TOKEN);

        // then
        verify(tokenRedisRepository).deleteRefreshToken(USER_ID);
        verify(tokenRedisRepository, never()).addToBlacklist(any(), anyLong());
    }
}
