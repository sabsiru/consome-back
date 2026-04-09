package consome.interfaces.comment.v1;

import consome.application.post.PostCommand;
import consome.application.post.PostFacade;
import consome.application.post.PostResult;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import consome.infrastructure.jwt.JwtProvider;
import consome.interfaces.comment.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import consome.config.TestBoardSetup;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class CommentV1ControllerE2eTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    PostFacade postFacade;

    @Autowired
    UserFacade userFacade;

    @Autowired
    TestBoardSetup testBoardSetup;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        testBoardSetup.setup();
    }

    Long 준비_유저_생성() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserRegisterCommand userRegisterCommand = UserRegisterCommand.of("user" + suffix, "nick" + suffix, "Password123", "user" + suffix + "@test.com");
        Long userId = userFacade.registerWithoutEmail(userRegisterCommand);
        User user = userRepository.findById(userId).orElseThrow();
        user.verifyEmail();
        userRepository.save(user);
        return userId;
    }

    Long 준비_게시글_생성(Long userId) {
        PostCommand postCommand = PostCommand.of(testBoardSetup.getBoardId(), testBoardSetup.getCategoryId(), userId, "title", "content");
        PostResult postResult = postFacade.post(postCommand);
        return postResult.postId();
    }

    private HttpHeaders authHeaders(Long userId) {
        String token = jwtProvider.createAccessToken(userId, Role.USER);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void 댓글_생성하면_200과_댓글정보를_반환한다() {
        // given
        Long userId = 준비_유저_생성();
        Long postId = 준비_게시글_생성(userId);
        CreateCommentRequest request = new CreateCommentRequest(null, "댓글 내용");
        String url = "/api/v1/posts/" + postId + "/comments";

        // when
        ResponseEntity<CommentListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders(userId)),
                CommentListResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CommentListResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.commentId()).isNotNull();
        assertThat(body.content()).isEqualTo("댓글 내용");
        assertThat(body.postId()).isEqualTo(postId);
        assertThat(body.userId()).isEqualTo(userId);
    }

    @Test
    void 댓글을_수정하면_컨텐츠가_변경된다() {
        //given
        Long userId = 준비_유저_생성();
        Long postId = 준비_게시글_생성(userId);
        CreateCommentRequest request = new CreateCommentRequest(null, "댓글 내용");
        String url = "/api/v1/posts/" + postId + "/comments";
        ResponseEntity<CommentListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders(userId)),
                CommentListResponse.class
        );
        Long commentId = response.getBody().commentId();

        //when
        EditCommentRequest editRequest = new EditCommentRequest("수정된 댓글 내용");
        String editUrl = "/api/v1/posts/" + postId + "/comments/" + commentId;

        ResponseEntity<EditCommentResponse> editResponse = restTemplate.exchange(
                editUrl,
                HttpMethod.PUT,
                new HttpEntity<>(editRequest, authHeaders(userId)),
                EditCommentResponse.class
        );
        EditCommentResponse body = editResponse.getBody();

        //then
        assertThat(body).isNotNull();
        assertThat(body.content()).isEqualTo("수정된 댓글 내용");
    }

    @Test
    void 댓글을_삭제하면_204를_반환한다() {
        // given
        Long userId = 준비_유저_생성();
        Long postId = 준비_게시글_생성(userId);
        CreateCommentRequest request = new CreateCommentRequest(null, "댓글 내용");
        String url = "/api/v1/posts/" + postId + "/comments";
        ResponseEntity<CommentListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders(userId)),
                CommentListResponse.class
        );
        Long commentId = response.getBody().commentId();

        // when
        String deleteUrl = "/api/v1/posts/" + postId + "/comments/" + commentId;
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(null, authHeaders(userId)),
                Void.class
        );

        // then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 댓글_좋아요() {
        //given
        Long userId = 준비_유저_생성();
        Long postId = 준비_게시글_생성(userId);
        CreateCommentRequest request = new CreateCommentRequest(null, "댓글 내용");
        String url = "/api/v1/posts/" + postId + "/comments";
        ResponseEntity<CommentListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders(userId)),
                CommentListResponse.class
        );
        Long commentId = response.getBody().commentId();

        Long otherUserId = 준비_유저_생성();

        //when
        String likeUrl = "/api/v1/posts/" + postId + "/comments/" + commentId + "/like";
        ResponseEntity<CommentStatResponse> likeResponse = restTemplate.exchange(
                likeUrl,
                HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(otherUserId)),
                CommentStatResponse.class
        );

        //then
        assertThat(likeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CommentStatResponse stat = likeResponse.getBody();
        assertThat(stat).isNotNull();
        assertThat(stat.likeCount()).isEqualTo(1L);
    }

    @Test
    void 댓글_싫어요() {
        // given
        Long userId = 준비_유저_생성();
        Long postId = 준비_게시글_생성(userId);
        CreateCommentRequest request = new CreateCommentRequest(null, "댓글 내용");
        String url = "/api/v1/posts/" + postId + "/comments";
        ResponseEntity<CommentListResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders(userId)),
                CommentListResponse.class
        );
        Long commentId = response.getBody().commentId();

        Long otherUserId = 준비_유저_생성();

        // when
        String dislikeUrl = "/api/v1/posts/" + postId + "/comments/" + commentId + "/dislike";
        ResponseEntity<CommentStatResponse> dislikeResponse = restTemplate.exchange(
                dislikeUrl,
                HttpMethod.POST,
                new HttpEntity<>(null, authHeaders(otherUserId)),
                CommentStatResponse.class
        );

        // then
        assertThat(dislikeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        CommentStatResponse stat = dislikeResponse.getBody();
        assertThat(stat).isNotNull();
        assertThat(stat.dislikeCount()).isEqualTo(1L);
    }
}
