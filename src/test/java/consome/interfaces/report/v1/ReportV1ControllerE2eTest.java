package consome.interfaces.report.v1;

import consome.application.post.PostCommand;
import consome.application.post.PostFacade;
import consome.application.post.PostResult;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.post.entity.Post;
import consome.interfaces.report.dto.CreateReportRequest;
import consome.interfaces.report.dto.ReportResponse;
import consome.domain.report.entity.ReportReason;
import consome.domain.report.entity.ReportTargetType;
import consome.interfaces.user.dto.UserLoginRequest;
import consome.interfaces.user.dto.UserLoginResponse;
import consome.config.TestBoardSetup;
import consome.infrastructure.mail.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class ReportV1ControllerE2eTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserFacade userFacade;

    @Autowired
    PostFacade postFacade;

    @Autowired
    TestBoardSetup testBoardSetup;

    @MockBean
    EmailService emailService;

    @BeforeEach
    void setUp() {
        testBoardSetup.setup();
    }

    private Long 유저_생성() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return userFacade.registerWithoutEmail(
                UserRegisterCommand.of("user" + suffix, "nick" + suffix, "Password123", "user" + suffix + "@test.com")
        );
    }

    private String 로그인_토큰(String loginId) {
        var res = restTemplate.postForEntity(
                "/api/v1/users/login",
                new UserLoginRequest(loginId, "Password123"),
                UserLoginResponse.class
        );
        return res.getBody().accessToken();
    }

    private Long 유저_생성_로그인ID반환() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "user" + suffix;
        userFacade.registerWithoutEmail(
                UserRegisterCommand.of(loginId, "nick" + suffix, "Password123", loginId + "@test.com")
        );
        return null; // not used
    }

    private record UserInfo(Long userId, String loginId) {}

    private UserInfo 유저_생성_상세() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String loginId = "user" + suffix;
        Long userId = userFacade.registerWithoutEmail(
                UserRegisterCommand.of(loginId, "nick" + suffix, "Password123", loginId + "@test.com")
        );
        return new UserInfo(userId, loginId);
    }

    private Long 게시글_생성(Long userId) {
        PostCommand cmd = PostCommand.of(testBoardSetup.getBoardId(), testBoardSetup.getCategoryId(), userId, "테스트 게시글", "내용");
        PostResult result = postFacade.post(cmd);
        return result.postId();
    }

    private HttpHeaders 인증_헤더(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("내 신고 목록 조회 시 200과 빈 페이지를 반환한다")
    void 내_신고_목록_빈_결과() {
        UserInfo user = 유저_생성_상세();
        String token = 로그인_토큰(user.loginId());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/reports/mine?userId=" + user.userId(),
                HttpMethod.GET,
                new HttpEntity<>(인증_헤더(token)),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\":0");
    }

    @Test
    @DisplayName("신고 후 내 신고 목록에 해당 신고가 포함된다")
    void 신고_후_내_목록_조회() {
        // 신고자
        UserInfo reporter = 유저_생성_상세();
        String token = 로그인_토큰(reporter.loginId());

        // 신고 대상 게시글 작성자
        Long targetUserId = 유저_생성();
        Long postId = 게시글_생성(targetUserId);

        // 신고 생성
        CreateReportRequest reportRequest = new CreateReportRequest(
                ReportTargetType.POST, postId, ReportReason.SPAM, "스팸 게시글"
        );
        restTemplate.exchange(
                "/api/v1/reports?userId=" + reporter.userId(),
                HttpMethod.POST,
                new HttpEntity<>(reportRequest, 인증_헤더(token)),
                ReportResponse.class
        );

        // 내 신고 목록 조회
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/reports/mine?userId=" + reporter.userId(),
                HttpMethod.GET,
                new HttpEntity<>(인증_헤더(token)),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\":1");
        assertThat(response.getBody()).contains("\"SPAM\"");
    }

    @Test
    @DisplayName("비로그인 시 내 신고 목록 조회 실패")
    void 비로그인_조회_실패() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/reports/mine?userId=1",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );

        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }
}
