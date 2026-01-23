package consome.interfaces.post.v1;

import consome.application.post.PostResult;
import consome.domain.post.repository.PostRepository;
import consome.interfaces.post.dto.*;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostV1ControllerE2eTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        EditRequest editRequest = new EditRequest("수정 대상 제목", 1L, "수정 후 내용");
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

    @Test
    @DisplayName("게시글 삭제 시 204 No Content를 반환하고 소프트 삭제(deleted=true)로 표시된다")
    void 게시글_삭제_성공_소프트삭제_검증() {
        // given - 회원가입
        UserRegisterRequest registerRequest = new UserRegisterRequest("delUser", "삭제닉", "Password123");
        ResponseEntity<UserRegisterResponse> userRes = restTemplate
                .postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);
        assertThat(userRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // and - 게시글 작성
        PostRequest postRequest = new PostRequest(1L, 1L, 1L, "삭제 대상", "삭제 전 내용");
        ResponseEntity<PostResult> postRes = restTemplate
                .postForEntity("/api/v1/posts", postRequest, PostResult.class);
        assertThat(postRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long postId = postRes.getBody().postId();

        // when - 삭제 요청
        ResponseEntity<Void> deleteRes = restTemplate.exchange(
                "/api/v1/posts/{postId}?userId={userId}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                postId, 1L
        );

        // then 1) HTTP 상태
        assertThat(deleteRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // then 2) @Where(clause="deleted=false") 적용으로, 기본 findById는 보이지 않아야 함
        assertThat(postRepository.findById(postId)).isEmpty();

        // then 3) 실제 DB에는 남아 있어야 하므로 deleted=true를 직접 확인 (native query)
        Integer deletedFlag = jdbcTemplate.queryForObject(
                "select deleted from post where id = ?",
                Integer.class,
                postId
        );
        // MySQL/TINYINT(1) 기준: true → 1
        assertThat(deletedFlag).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 시 200 OK와 PostStatResponse(likeCount 증가)를 반환한다")
    void 게시글_좋아요_성공() {
        // given - 회원가입 + 게시글 작성
        UserRegisterRequest registerRequest = new UserRegisterRequest("likeUser", "좋아요닉", "Password123");
        restTemplate.postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);

        PostRequest postRequest = new PostRequest(1L, 1L, 1L, "좋아요 대상", "내용");
        ResponseEntity<PostResult> postRes = restTemplate
                .postForEntity("/api/v1/posts", postRequest, PostResult.class);
        Long postId = postRes.getBody().postId();

        // when
        ResponseEntity<PostStatResponse> likeRes = restTemplate.postForEntity(
                "/api/v1/posts/{postId}/like?userId={userId}",
                null, // body 없음
                PostStatResponse.class,
                postId, 1L
        );

        // then
        assertThat(likeRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(likeRes.getBody()).isNotNull();
        assertThat(likeRes.getBody().postId()).isEqualTo(postId);
        assertThat(likeRes.getBody().likeCount()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("싫어요 시 200 OK와 PostStatResponse(dislikeCount 증가)를 반환한다")
    void 게시글_싫어요_성공() {
        // given - 회원가입 + 게시글 작성
        UserRegisterRequest registerRequest = new UserRegisterRequest("dislikeUser", "싫어요닉", "Password123");
        restTemplate.postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);

        PostRequest postRequest = new PostRequest(1L, 1L, 1L, "싫어요 대상", "내용");
        ResponseEntity<PostResult> postRes = restTemplate
                .postForEntity("/api/v1/posts", postRequest, PostResult.class);
        Long postId = postRes.getBody().postId();

        // when
        ResponseEntity<PostStatResponse> dislikeRes = restTemplate.postForEntity(
                "/api/v1/posts/{postId}/dislike?userId={userId}",
                null,
                PostStatResponse.class,
                postId, 1L
        );

        // then
        assertThat(dislikeRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dislikeRes.getBody()).isNotNull();
        assertThat(dislikeRes.getBody().postId()).isEqualTo(postId);
        assertThat(dislikeRes.getBody().dislikeCount()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("게시글 상세 진입 시 200 OK와 PostDetailResponse를 반환하고 viewCount가 반영된다")
    void 게시글_상세조회_성공() {
        // given - 회원가입 + 게시글 작성
        UserRegisterRequest registerRequest = new UserRegisterRequest("viewer", "뷰닉", "Password123");
        restTemplate.postForEntity("/api/v1/users", registerRequest, UserRegisterResponse.class);

        String content = "상세 내용 확인";
        PostRequest postRequest = new PostRequest(1L, 1L, 1L, "상세 제목", content);
        ResponseEntity<PostResult> postRes = restTemplate
                .postForEntity("/api/v1/posts", postRequest, PostResult.class);
        Long postId = postRes.getBody().postId();

        // when - 상세 진입(조회수 증가 포함)
        ResponseEntity<PostDetailResponse> detailRes = restTemplate.getForEntity(
                "/api/v1/posts/{postId}/view?userId={userId}",
                PostDetailResponse.class,
                postId, 1L
        );

        // then
        assertThat(detailRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detailRes.getBody()).isNotNull();
        assertThat(detailRes.getBody().postId()).isEqualTo(postId);
        assertThat(detailRes.getBody().content()).isEqualTo(content);
        assertThat(detailRes.getBody().viewCount()).isGreaterThanOrEqualTo(1);
    }
}