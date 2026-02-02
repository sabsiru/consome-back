package consome.application.post;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.comment.repository.CommentReactionRepository;
import consome.domain.comment.repository.CommentRepository;
import consome.domain.comment.CommentService;
import consome.domain.point.Point;
import consome.domain.point.repository.PointHistoryRepository;
import consome.domain.point.PointHistoryType;
import consome.domain.point.repository.PointRepository;
import consome.domain.post.PostService;
import consome.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostFacadeIntegrationTest {

    @Autowired
    private PostFacade postFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentReactionRepository commentReactionRepository;

    private UserRegisterCommand userRegisterCommand;

    private long boardId = 1L;
    private long categoryId = 1L;

    @BeforeEach
    void setUp() {
        userRegisterCommand = UserRegisterCommand.of("testid", "테스트닉네임", "Password123");
        userRepository.deleteAll();
    }

    @DisplayName("게시글을 작성하면 포인트가 적립되고 게시글이 생성된다")
    @Test
    void createPost_V1_success() {
        // given
        Long userId = userFacade.register(userRegisterCommand);
        PostCommand command = PostCommand.of(boardId, categoryId, userId, "제목입니다", "내용입니다");

        int initialPoint = PointHistoryType.REGISTER.getPoint();
        int postPoint = PointHistoryType.POST_WRITE.getPoint();
        int expectedPoint = initialPoint + postPoint;

        // when
        PostResult result = postFacade.post(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.postId()).isNotNull();
        assertThat(postService.getPost(result.postId()))
                .isNotNull()
                .extracting("title", "content", "userId")
                .containsExactly("제목입니다", "내용입니다", userId);
        // and
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(point.getUserPoint()).isEqualTo(expectedPoint);
    }
}
