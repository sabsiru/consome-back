package consome.interfaces.post.v1;

import consome.application.post.PostResult;
import consome.domain.post.repository.PostRepository;
import consome.interfaces.post.dto.EditRequest;
import consome.interfaces.post.dto.EditResponse;
import consome.interfaces.post.dto.PostRequest;
import consome.interfaces.user.dto.UserRegisterRequest;
import consome.interfaces.user.dto.UserRegisterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostRepository postRepository;

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

    @Test
    @DisplayName("게시글 수정 시 200 OK와 EditResponse를 반환한다")
    void 게시글_수정_성공() throws Exception {
        // given - 회원가입
        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "editUser",
                "수정닉네임",
                "Password123"
        );
        ResponseEntity<UserRegisterResponse> userResponse = restTemplate
                .postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userResponse.getBody()).isNotNull();

        // and - 게시글 작성
        PostRequest postRequest = new PostRequest(
                1L, 1L, 1L,
                "수정 대상 제목",
                "수정 전 내용"
        );

        ResponseEntity<PostResult> postResponse = restTemplate
                .postForEntity("/api/v1/posts", postRequest, PostResult.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getBody()).isNotNull();
        Long postId = postResponse.getBody().postId();
        assertThat(postId).isNotNull();

        // when - 게시글 수정
        EditRequest editRequest = new EditRequest("수정된 내용");
        ResponseEntity<EditResponse> editRes = restTemplate.exchange(
                "/api/v1/posts/{postId}?userId={userId}",
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                EditResponse.class,
                postId, 1L
        );

        // then
        postRepository.findById(postId).ifPresent(post -> {
            assertThat(post.getContent()).isEqualTo("수정된 내용");
        });
        assertThat(editRes.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}