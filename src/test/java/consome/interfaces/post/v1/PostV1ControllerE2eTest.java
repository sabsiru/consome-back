package consome.interfaces.post.v1;

import consome.interfaces.post.dto.PostRequest;
import consome.interfaces.user.dto.UserRegisterRequest;
import consome.interfaces.user.dto.UserRegisterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("회원가입 후 게시글 작성 시 201 Created 응답을 반환한다")
    void 회원가입_후_게시글작성_성공() throws Exception {
        // given - 회원가입
        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "testUser",
                "테스트닉네임",
                "Password123"
        );

        ResponseEntity<UserRegisterResponse> response = restTemplate
                .postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        // when - 게시글 작성
        PostRequest postRequest = new PostRequest(
                1L,
                1L,
                1L,
                "테스트 게시글 제목",
                "테스트 게시글 내용"
        );


        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity("/api/v1/posts", postRequest, Void.class);

        // then
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}