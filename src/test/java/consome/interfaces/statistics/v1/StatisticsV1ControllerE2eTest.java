package consome.interfaces.statistics.v1;

import consome.application.user.UserFacade;
import consome.application.user.UserLoginCommand;
import consome.application.user.UserLoginResult;
import consome.application.user.UserRegisterCommand;
import consome.domain.email.EmailVerificationService;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.UserRepository;
import consome.infrastructure.mail.EmailService;
import consome.interfaces.statistics.dto.HourlyActivityResponse;
import consome.interfaces.statistics.dto.PopularKeywordsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class StatisticsV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    private HttpHeaders adminAuthHeaders() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "admin" + suffix;
        String password = "Password123";
        userFacade.registerWithoutEmail(
                UserRegisterCommand.of(loginId, "admin" + suffix, password, loginId + "@test.com")
        );
        User user = userRepository.findByLoginId(loginId).orElseThrow();
        user.updateRole(Role.ADMIN);
        userRepository.save(user);

        UserLoginResult login = userFacade.login(new UserLoginCommand(loginId, password));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login.accessToken());
        return headers;
    }

    @Test
    @DisplayName("인기 검색어 - 기본 파라미터로 200 응답 (빈 결과 허용)")
    void getPopularKeywords_default() {
        ResponseEntity<PopularKeywordsResponse> response = restTemplate.getForEntity(
                "/api/v1/statistics/popular-keywords",
                PopularKeywordsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().period()).isEqualTo("hour");
        assertThat(response.getBody().limit()).isEqualTo(10);
        assertThat(response.getBody().items()).isNotNull();
    }

    @Test
    @DisplayName("인기 검색어 - period=day, limit=5 파라미터 반영")
    void getPopularKeywords_dayLimit5() {
        ResponseEntity<PopularKeywordsResponse> response = restTemplate.getForEntity(
                "/api/v1/statistics/popular-keywords?period=day&limit=5",
                PopularKeywordsResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().period()).isEqualTo("day");
        assertThat(response.getBody().limit()).isEqualTo(5);
    }

    @Test
    @DisplayName("시간대별 활동량 - 관리자 인증 시 기본 파라미터: 7일 ALL 집계")
    void getHourlyActivity_default() {
        ResponseEntity<HourlyActivityResponse> response = restTemplate.exchange(
                "/api/v1/admin/statistics/hourly-activity",
                HttpMethod.GET,
                new HttpEntity<>(adminAuthHeaders()),
                HourlyActivityResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().days()).isEqualTo(7);
        assertThat(response.getBody().aggregated()).isTrue();
        assertThat(response.getBody().rows()).hasSize(7);
        assertThat(response.getBody().rows().get(0).hours()).hasSize(24);
    }

    @Test
    @DisplayName("시간대별 활동량 - 단일 타입 지정 시 aggregated=false")
    void getHourlyActivity_singleType() {
        ResponseEntity<HourlyActivityResponse> response = restTemplate.exchange(
                "/api/v1/admin/statistics/hourly-activity?days=3&type=POST",
                HttpMethod.GET,
                new HttpEntity<>(adminAuthHeaders()),
                HourlyActivityResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().days()).isEqualTo(3);
        assertThat(response.getBody().aggregated()).isFalse();
        assertThat(response.getBody().rows()).hasSize(3);
    }

    @Test
    @DisplayName("히트맵은 비인증 호출 시 차단 (401 또는 403)")
    void unauthorized_blocked() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/admin/statistics/hourly-activity",
                String.class
        );
        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }
}
