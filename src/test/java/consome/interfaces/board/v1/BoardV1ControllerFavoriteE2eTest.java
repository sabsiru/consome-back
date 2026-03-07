package consome.interfaces.board.v1;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.interfaces.board.dto.FavoriteBoardResponse;
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
import consome.infrastructure.mail.EmailService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class BoardV1ControllerFavoriteE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserFacade userFacade;

    @MockBean
    private EmailService emailService;

    private static final Long BOARD_ID = 1L;

    private String 로그인_토큰_획득() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        userFacade.registerWithoutEmail(
                UserRegisterCommand.of("user" + suffix, "nick" + suffix, "Password123", "user" + suffix + "@test.com")
        );
        UserLoginRequest loginRequest = new UserLoginRequest("user" + suffix, "Password123");
        ResponseEntity<UserLoginResponse> loginRes = restTemplate
                .postForEntity("/api/v1/users/login", loginRequest, UserLoginResponse.class);
        return loginRes.getBody().accessToken();
    }

    private HttpHeaders 인증_헤더(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("즐겨찾기 추가 시 201 Created를 반환한다")
    void 즐겨찾기_추가_성공() {
        String token = 로그인_토큰_획득();
        HttpEntity<Void> request = new HttpEntity<>(인증_헤더(token));

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/boards/" + BOARD_ID + "/favorites",
                HttpMethod.POST,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("즐겨찾기 추가 후 목록 조회 시 해당 게시판이 포함된다")
    void 즐겨찾기_목록_조회_성공() {
        String token = 로그인_토큰_획득();
        HttpEntity<Void> request = new HttpEntity<>(인증_헤더(token));

        restTemplate.exchange("/api/v1/boards/" + BOARD_ID + "/favorites", HttpMethod.POST, request, Void.class);

        ResponseEntity<FavoriteBoardResponse[]> listRes = restTemplate.exchange(
                "/api/v1/boards/favorites",
                HttpMethod.GET,
                request,
                FavoriteBoardResponse[].class
        );

        assertThat(listRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listRes.getBody()).isNotNull();
        assertThat(listRes.getBody()).anyMatch(b -> b.boardId().equals(BOARD_ID));
    }

    @Test
    @DisplayName("즐겨찾기 삭제 시 204 No Content를 반환한다")
    void 즐겨찾기_삭제_성공() {
        String token = 로그인_토큰_획득();
        HttpEntity<Void> request = new HttpEntity<>(인증_헤더(token));

        restTemplate.exchange("/api/v1/boards/" + BOARD_ID + "/favorites", HttpMethod.POST, request, Void.class);

        ResponseEntity<Void> deleteRes = restTemplate.exchange(
                "/api/v1/boards/" + BOARD_ID + "/favorites",
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(deleteRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("이미 즐겨찾기한 게시판에 다시 추가하면 409를 반환한다")
    void 즐겨찾기_중복_추가_실패() {
        String token = 로그인_토큰_획득();
        HttpEntity<Void> request = new HttpEntity<>(인증_헤더(token));

        restTemplate.exchange("/api/v1/boards/" + BOARD_ID + "/favorites", HttpMethod.POST, request, Void.class);

        ResponseEntity<Void> duplicateRes = restTemplate.exchange(
                "/api/v1/boards/" + BOARD_ID + "/favorites",
                HttpMethod.POST,
                request,
                Void.class
        );

        assertThat(duplicateRes.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
