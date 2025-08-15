package consome.interfaces.user.v1;

import consome.interfaces.error.ErrorResponse;
import consome.interfaces.user.dto.UserLoginRequest;
import consome.interfaces.user.dto.UserLoginResponse;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class UserV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private UserRegisterRequest registerRequest;

    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String uniqueLoginId = "tu" + suffix; // length 10, within 4~20
        String uniqueNickname = "테스트유저" + suffix; // 닉네임 중복 회피
        registerRequest = new UserRegisterRequest(
                uniqueLoginId, uniqueNickname, "Password123"
        );
    }

    @Test
    @DisplayName("성공: 올바른 회원가입 요청은 201과 메시지를 반환한다")
    void registerSuccess() {
        ResponseEntity<UserRegisterResponse> response = restTemplate
                .postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
                .isEqualTo("회원가입이 성공적으로 완료되었습니다.");
    }

    @Test
    @DisplayName("실패: 중복 아이디로 회원가입시 409와 에러 코드를 반환한다")
    void registerDuplicate() {
        ResponseEntity<UserRegisterResponse> success = restTemplate
                .postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);
        assertThat(success.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);

        String suffix2 = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        UserRegisterRequest duplicateLoginIdReq = new UserRegisterRequest(
                registerRequest.loginId(), // 같은 로그인 아이디
                "테스트유저" + suffix2,     // 다른 닉네임 → 닉네임 중복 회피
                registerRequest.password()
        );

        ResponseEntity<ErrorResponse> error = restTemplate
                .postForEntity("/api/v1/users", duplicateLoginIdReq, ErrorResponse.class);

        assertThat(error.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
        assertThat(error.getBody()).isNotNull();
        assertThat(error.getBody().getCode()).isEqualTo("DUPLICATE_LOGIN_ID");
        assertThat(error.getBody().getMessage()).contains("이미 사용 중인 아이디입니다.");
    }

    @Test
    @DisplayName("성공: 로그인 성공시 사용자의 정보와 포인트를 반환한다.")
    void loginSuccess() {
        restTemplate.postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);
        UserLoginRequest loginRequest = new UserLoginRequest(
                registerRequest.loginId(), registerRequest.password()
        );

        ResponseEntity<UserLoginResponse> success = restTemplate
                .postForEntity("/api/v1/users/login", loginRequest, UserLoginResponse.class);
        assertThat(success.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);

        UserLoginResponse body = success.getBody();

        assertThat(body.loginId()).isEqualTo(registerRequest.loginId());
        assertThat(body.point()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("실패: 잘못된 비밀번호로 로그인 시 401과 에러 코드를 반환한다")
    void loginFailure() {
        restTemplate.postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);

        UserLoginRequest badRequest = new UserLoginRequest(
                registerRequest.loginId(),
                "IncorrectPassword!"
        );

        ResponseEntity<ErrorResponse> response = restTemplate
                .postForEntity("/api/v1/users/login", badRequest, ErrorResponse.class);

        assertThat(response.getStatusCode())
                .isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode())
                .isEqualTo("INVALID_CREDENTIALS");
        assertThat(response.getBody().getMessage())
                .contains("아이디 또는 비밀번호가 일치하지 않습니다");
    }


}