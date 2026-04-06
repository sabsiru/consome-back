package consome.application.comment;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.comment.CommentService;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.config.TestBoardSetup;
import consome.infrastructure.mail.EmailService;
import consome.domain.email.EmailVerificationService;
import consome.infrastructure.redis.EmailVerificationRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class CommentFacadeIntegrationTest {

    @Autowired
    private CommentFacade commentFacade;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private TestBoardSetup testBoardSetup;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        testBoardSetup.setup();
    }


    @Test
    void 댓글_페이징_통합_테스트() {
        // given
        UserRegisterCommand registerCommand = new UserRegisterCommand(
                "testerID",
                "테스터",
                "Password123",
                "tester@test.com"
        );
        Long register = userFacade.registerWithoutEmail(registerCommand);
        Post post = postService.post(testBoardSetup.getBoardId(), testBoardSetup.getCategoryId(), register, "testTitle",
                "testContent");

        for (int i = 1; i <= 100; i++) {
            commentService.comment(post.getId(), register, null, "댓글 " +
                    i);
        }

        // when
        CommentPageResult firstResult = commentFacade.listByPost(
                post.getId(),
                null,  // userId 없이 조회
                PageRequest.of(0, 50)
        );

        CommentPageResult secondResult = commentFacade.listByPost(
                post.getId(),
                null,
                PageRequest.of(1, 50)
        );

        // then
        assertThat(firstResult.comments().getContent()).hasSize(50);
        assertThat(secondResult.comments().getContent()).hasSize(50);
        assertThat(firstResult.comments().getTotalElements()).isEqualTo(100);
    }

    @Test
    void 댓글_추천_상태_조회_테스트() {
        // given
        UserRegisterCommand registerCommand = new UserRegisterCommand(
                "testerID",
                "테스터",
                "Password123",
                "tester@test.com"
        );
        Long register = userFacade.registerWithoutEmail(registerCommand);
        Post post = postService.post(testBoardSetup.getBoardId(), testBoardSetup.getCategoryId(), register, "testTitle",
                "testContent");
        CommentResult comment = commentFacade.comment(post.getId(), register,
                null, "테스트 댓글");

        // 댓글 추천
        commentFacade.like(comment.commentId(), register);

        // when
        CommentPageResult pageResult = commentFacade.listByPost(
                post.getId(),
                register,  // userId로 조회
                PageRequest.of(0, 50)
        );

        // then
        CommentListResult result = pageResult.comments().getContent().get(0);
        assertThat(result.hasLiked()).isTrue();
        assertThat(result.hasDisliked()).isFalse();
    }
}