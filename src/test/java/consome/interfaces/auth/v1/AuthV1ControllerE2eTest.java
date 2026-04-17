package consome.interfaces.auth.v1;

import consome.application.user.UserFacade;
import consome.application.user.UserLoginCommand;
import consome.application.user.UserLoginResult;
import consome.application.user.UserRegisterCommand;
import consome.domain.email.EmailVerificationService;
import consome.infrastructure.mail.EmailService;
import consome.interfaces.auth.dto.TokenRefreshRequest;
import consome.interfaces.auth.dto.TokenRefreshResponse;
import consome.interfaces.error.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class AuthV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserFacade userFacade;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    private UserLoginResult createUserAndLogin() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "user" + suffix;
        String password = "Password123";
        userFacade.registerWithoutEmail(
                UserRegisterCommand.of(loginId, "nick" + suffix, password, loginId + "@test.com")
        );
        return userFacade.login(new UserLoginCommand(loginId, password));
    }

    @Test
    @DisplayName("로그인 후 refreshToken으로 새 accessToken을 발급받는다")
    void 토큰_갱신_성공() {
        // given
        UserLoginResult loginResult = createUserAndLogin();
        TokenRefreshRequest request = new TokenRefreshRequest(loginResult.refreshToken());

        // when
        ResponseEntity<TokenRefreshResponse> response = restTemplate
                .postForEntity("/api/v1/auth/refresh", request, TokenRefreshResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();
        // refreshToken은 항상 새로 발급 (Rotation)
        assertThat(response.getBody().refreshToken()).isNotEqualTo(loginResult.refreshToken());
    }

    @Test
    @DisplayName("갱신된 refreshToken으로 다시 갱신할 수 있다 (Rotation)")
    void 토큰_로테이션_성공() {
        // given
        UserLoginResult loginResult = createUserAndLogin();
        TokenRefreshRequest firstRequest = new TokenRefreshRequest(loginResult.refreshToken());

        ResponseEntity<TokenRefreshResponse> firstResponse = restTemplate
                .postForEntity("/api/v1/auth/refresh", firstRequest, TokenRefreshResponse.class);
        String newRefreshToken = firstResponse.getBody().refreshToken();

        // when
        TokenRefreshRequest secondRequest = new TokenRefreshRequest(newRefreshToken);
        ResponseEntity<TokenRefreshResponse> secondResponse = restTemplate
                .postForEntity("/api/v1/auth/refresh", secondRequest, TokenRefreshResponse.class);

        // then
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponse.getBody().accessToken()).isNotBlank();
    }

    @Test
    @DisplayName("이전 refreshToken은 사용 불가 (탈취 감지)")
    void 이전_리프레시_토큰_사용시_실패() {
        // given
        UserLoginResult loginResult = createUserAndLogin();
        String originalRefreshToken = loginResult.refreshToken();

        // 한 번 갱신하여 기존 토큰 무효화
        ResponseEntity<TokenRefreshResponse> refreshResponse = restTemplate.postForEntity(
                "/api/v1/auth/refresh",
                new TokenRefreshRequest(originalRefreshToken),
                TokenRefreshResponse.class);
        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // when - 이전 토큰으로 다시 시도
        ResponseEntity<ErrorResponse> response = restTemplate
                .postForEntity("/api/v1/auth/refresh",
                        new TokenRefreshRequest(originalRefreshToken), ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("잘못된 refreshToken으로 갱신 시 401 응답")
    void 잘못된_토큰으로_갱신_실패() {
        // when
        TokenRefreshRequest request = new TokenRefreshRequest("invalid-token");
        ResponseEntity<ErrorResponse> response = restTemplate
                .postForEntity("/api/v1/auth/refresh", request, ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void 로그아웃_성공() {
        // given
        UserLoginResult loginResult = createUserAndLogin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResult.accessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // when
        ResponseEntity<Void> response = restTemplate
                .exchange("/api/v1/auth/logout", HttpMethod.POST, request, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("인증 없이 로그아웃 시 401 응답")
    void 인증없이_로그아웃_실패() {
        // when
        ResponseEntity<Void> response = restTemplate
                .postForEntity("/api/v1/auth/logout", null, Void.class);

        // then
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("잘못된 Authorization 헤더로 로그아웃 시 400 응답")
    void 잘못된_헤더로_로그아웃_실패() {
        // given
        UserLoginResult loginResult = createUserAndLogin();

        // when - "Bearer " 접두사 없이 토큰만 전송
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "InvalidPrefix " + loginResult.accessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate
                .exchange("/api/v1/auth/logout", HttpMethod.POST, request, Void.class);

        // then - JwtAuthenticationFilter에서 인증 실패 → 401/403
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("로그아웃 후 동일 accessToken으로 인증이 실패한다")
    void 로그아웃_후_토큰_무효화() {
        // given
        UserLoginResult loginResult = createUserAndLogin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResult.accessToken());

        // 로그아웃
        restTemplate.exchange("/api/v1/auth/logout", HttpMethod.POST,
                new HttpEntity<>(headers), Void.class);

        // when - 같은 토큰으로 인증 필요한 API 호출
        ResponseEntity<Void> response = restTemplate
                .exchange("/api/v1/auth/logout", HttpMethod.POST,
                        new HttpEntity<>(headers), Void.class);

        // then - 블랙리스트에 의해 인증 실패
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }
}
