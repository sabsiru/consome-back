package consome.interfaces.user.v1;

import consome.interfaces.error.ErrorResponse;
import consome.interfaces.user.dto.UserRegisterRequest;
import consome.interfaces.user.dto.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class UserV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private UserRegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new UserRegisterRequest(
                "testuser", "테스트유저", "Password123"
        );
    }

    @Test
    @DisplayName("성공: 올바른 회원가입 요청은 201과 메시지를 반환한다")
    void registerSuccess() {
        ResponseEntity<UserRegisterResponse> response = restTemplate
                .postForEntity("/api/v1/users", validRequest, UserRegisterResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
                .isEqualTo("회원가입이 성공적으로 완료되었습니다.");
    }

    @Test
    @DisplayName("실패: 중복 아이디로 회원가입시 409와 에러 코드를 반환한다")
    void registerDuplicate() {
        ResponseEntity<UserRegisterResponse> success = restTemplate
                .postForEntity("/api/v1/users", validRequest, UserRegisterResponse.class);
        assertThat(success.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);

        ResponseEntity<ErrorResponse> error = restTemplate
                .postForEntity("/api/v1/users", validRequest, ErrorResponse.class);

        assertThat(error.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
        assertThat(error.getBody()).isNotNull();
        assertThat(error.getBody().getCode()).isEqualTo("DUPLICATE_LOGIN_ID");
        assertThat(error.getBody().getMessage()).contains("이미 사용 중인 아이디입니다.");
    }
}