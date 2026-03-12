package consome.interfaces.user.v1;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.user.SuspensionType;
import consome.domain.user.UserService;
import consome.infrastructure.mail.EmailService;
import consome.interfaces.error.ErrorResponse;
import consome.interfaces.user.dto.UserLoginRequest;
import consome.interfaces.user.dto.UserLoginResponse;
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
class SuspensionE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserService userService;

    @MockBean
    private EmailService emailService;

    private String suffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private Long createUser(String suffix) {
        return userFacade.registerWithoutEmail(
                UserRegisterCommand.of("user" + suffix, "닉네임" + suffix, "Password123", "user" + suffix + "@test.com"));
    }

    private String login(String suffix) {
        UserLoginRequest loginRequest = new UserLoginRequest("user" + suffix, "Password123");
        ResponseEntity<UserLoginResponse> response = restTemplate
                .postForEntity("/api/v1/users/login", loginRequest, UserLoginResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().accessToken();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("제재된 유저가 API 호출 시 403과 USER_SUSPENDED 코드를 반환한다")
    void 제재_유저_API_호출_차단() {
        // given
        String s = suffix();
        Long userId = createUser(s);
        String token = login(s);

        // 제재 적용
        userService.suspend(userId, SuspensionType.DAY_1, "테스트 제재", null, 999L);

        // when - 제재된 상태로 인증 필요한 API 호출
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_SUSPENDED");
        assertThat(response.getBody().getMessage()).contains("테스트 제재");
    }

    @Test
    @DisplayName("영구 정지 유저가 API 호출 시 403과 USER_BANNED 코드를 반환한다")
    void 영구정지_유저_API_호출_차단() {
        // given
        String s = suffix();
        Long userId = createUser(s);
        String token = login(s);

        // 영구 정지
        userService.suspend(userId, SuspensionType.PERMANENT, "영구 정지 사유", null, 999L);

        // when
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_BANNED");
        assertThat(response.getBody().getMessage()).contains("영구 정지 사유");
    }

    @Test
    @DisplayName("제재된 유저가 로그인 시도 시 403과 제재 정보를 반환한다")
    void 제재_유저_로그인_차단() {
        // given
        String s = suffix();
        Long userId = createUser(s);
        userService.suspend(userId, SuspensionType.DAY_7, "로그인 차단 테스트", null, 999L);

        // when
        UserLoginRequest loginRequest = new UserLoginRequest("user" + s, "Password123");
        ResponseEntity<ErrorResponse> response = restTemplate
                .postForEntity("/api/v1/users/login", loginRequest, ErrorResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_SUSPENDED");
    }

    @Test
    @DisplayName("제재 해제 후 API 호출이 정상적으로 동작한다")
    void 제재_해제_후_정상_동작() {
        // given
        String s = suffix();
        Long userId = createUser(s);
        String token = login(s);

        userService.suspend(userId, SuspensionType.DAY_1, "일시 제재", null, 999L);

        // 제재 확인
        ResponseEntity<ErrorResponse> blocked = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                ErrorResponse.class);
        assertThat(blocked.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // when - 제재 해제
        userService.unsuspend(userId);

        // then - 정상 동작
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
    }
}
