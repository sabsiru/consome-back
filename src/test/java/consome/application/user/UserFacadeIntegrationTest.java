package consome.application.user;

import consome.domain.point.*;
import consome.domain.post.Post;
import consome.domain.post.PostService;
import consome.domain.post.PostStat;
import consome.domain.user.User;
import consome.domain.user.UserRepository;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    private UserCommand userCommand;

    @BeforeEach
    void setUp() {
        userCommand = UserCommand.of("testid", "테스트닉네임", "password123");
        userRepository.deleteAll();
    }

    @Test
    void 회원가입_요청시_사용자가_생성되고_포인트가_초기화되며_히스토리가_생성된다() {
        // when
        Long userId = userFacade.register(userCommand);
        int initialPoint = PointHistoryType.REGISTER.getPoint();
        int beforePoint = 0;
        int afterPoint = beforePoint + initialPoint;

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getId()).isEqualTo(userId);
        assertThat(savedUser.getLoginId()).isEqualTo(userCommand.getLoginId());
        assertThat(savedUser.getNickname()).isEqualTo(userCommand.getNickname());

        Optional<PointHistory> history = pointHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

        assertThat(history.get().getUserId()).isEqualTo(userId);
        assertThat(history.get().getBeforePoint()).isEqualTo(beforePoint);
        assertThat(history.get().getAfterPoint()).isEqualTo(afterPoint);
        assertThat(history.get().getType()).isEqualTo(PointHistoryType.REGISTER);
        assertThat(history.get().getDescription()).contains(PointHistoryType.REGISTER.getDescription());

        Point userPoint = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(userPoint.getPoint()).isEqualTo(initialPoint);
    }

    @Test
    void 유저_loginId가_중복일시_예외발생() {
        //given
        userFacade.register(userCommand);
        UserCommand duplicateUserCommand = UserCommand.of("testid", "다른닉네임", "다른비밀번호");

        //then
        assertThatThrownBy(() -> userFacade.register(duplicateUserCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용 중인 아이디입니다.");
    }

    @Test
    void 유저_닉네임이_중복일시_예외발생() {
        //given
        userFacade.register(userCommand);
        UserCommand duplicateUserCommand = UserCommand.of("다른아이디", "테스트닉네임", "다른비밀번호");

        //then
        assertThatThrownBy(() -> userFacade.register(duplicateUserCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");

    }

    @Test
    void 게시글_작성후_포인트조회() {
        // given
        Long userId = userFacade.register(userCommand);
        long boardId = 1L;
        long categoryId = 1L;
        int initialPoint = PointHistoryType.REGISTER.getPoint();
        int postPoint = PointHistoryType.POST_WRITE.getPoint();
        int expectedPoint = initialPoint + postPoint;
        Post post = Post.write(boardId, categoryId, userId, "테스트 제목", "테스트 내용");
        // when
        userFacade.write(post);

        // then
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(point.getPoint()).isEqualTo(expectedPoint);
    }

    @Test
    void 게시글_정상_수정_확인() {
        // given
        Long userId = userFacade.register(userCommand);
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, userId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        // when
        Post edited = userFacade.edit("수정된 제목", "수정된 내용", post.getId(), userId);

        // then
        assertThat(post.getTitle()).isEqualTo(edited.getTitle());
        assertThat(post.getContent()).isEqualTo(edited.getContent());
    }

    @Test
    void 게시글_삭제시_포인트_차감_확인() {
        //given
        Long userId = userFacade.register(userCommand);
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, userId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        //when
        userFacade.delete(post.getId(), userId);

        //then
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(point.getPoint()).isEqualTo(100);
    }

    @Test
    void 게시글_작성후_다른유저로_좋아요시_포인트증가_확인() {
        //given
        Long authorId = userFacade.register(userCommand);
        Long otherUser = userFacade.register(UserCommand.of("likerid", "좋아요닉네임", "likerpassword"));
        long boardId = 1L;
        long categoryId = 1L;
        int initialPoint = PointHistoryType.REGISTER.getPoint();
        int postPoint = PointHistoryType.POST_WRITE.getPoint();
        int likePoint = PointHistoryType.POST_LIKE.getPoint();
        int expectedPoint = initialPoint + postPoint + likePoint;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        userFacade.write(post);
        //when

        userFacade.like(post, otherUser);

        //then
        Point authorPoint = pointRepository.findByUserId(authorId).orElseThrow();
        assertThat(authorPoint.getPoint()).isEqualTo(expectedPoint);
    }

    @Test
    void 게시글_좋아요_중복시_예외발생() {
        //given
        Long authorId = userFacade.register(userCommand);
        Long otherUser = userFacade.register(UserCommand.of("likerid", "좋아요닉네임", "likerpassword"));
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        //when
        userFacade.like(post, otherUser);

        //then
        assertThatThrownBy(() -> userFacade.like(post, otherUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 좋아요를 누른 게시글입니다.");

    }

    @Test
    void 게시글_싫어요_정상차감_확인() {
        //given
        Long authorId = userFacade.register(userCommand);
        Long otherUser = userFacade.register(UserCommand.of("dislikerid", "싫어요닉네임", "dislikerpassword"));
        long boardId = 1L;
        long categoryId = 1L;
        int initialPoint = PointHistoryType.REGISTER.getPoint();
        int postPoint = PointHistoryType.POST_WRITE.getPoint();
        int dislikePoint = PointHistoryType.POST_DISLIKE.getPoint();
        int expectedPoint = initialPoint + postPoint - dislikePoint;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        //when
        userFacade.dislike(post, otherUser);

        //then
        Point authorPoint = pointRepository.findByUserId(authorId).orElseThrow();
        assertThat(authorPoint.getPoint()).isEqualTo(expectedPoint);
    }

    @Test
    void 게시글_싫어요_중복시_예외발생() {
        //given
        Long authorId = userFacade.register(userCommand);
        Long otherUser = userFacade.register(UserCommand.of("dislikerid", "싫어요닉네임", "dislikerpassword"));
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        //when
        userFacade.dislike(post, otherUser);

        //then
        assertThatThrownBy(() -> userFacade.dislike(post, otherUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 싫어요를 누른 게시글입니다.");
    }

    @Test
    void 게시글_수정시_작성자가_아니면_예외발생() {
        //given
        Long authorId = userFacade.register(userCommand);
        Long otherUser = userFacade.register(UserCommand.of("otherid", "다른닉네임", "otherpassword"));
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        //when

        //then
        assertThatThrownBy(() -> userFacade.edit("수정된 제목", "수정된 내용", post.getId(), otherUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("작성자만 게시글을 수정할 수 있습니다.");
    }

    @Test
    void 게시글_삭제시_작성자가_아니면_예외발생() {
        //given
        Long authorId = userFacade.register(userCommand);
        Long otherUser = userFacade.register(UserCommand.of("otherid", "다른닉네임", "otherpassword"));
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        userFacade.write(post);

        //then
        assertThatThrownBy(() -> userFacade.delete(post.getId(), otherUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("작성자만 게시글을 삭제할 수 있습니다.");
    }

    @Test
    void 게시글_조회수_증가_확인() {
        // given
        Long authorId = userFacade.register(userCommand);
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        Post savedPost = userFacade.write(post);
        String userIp = "127.0.0.1";

        // when
        userFacade.increaseViewCount(savedPost.getId(), authorId, userIp);

        // then
        PostStat postStat = postService.getPostStat(savedPost.getId());
        assertThat(postStat.getViewCount()).isEqualTo(1);
    }

    @Test
    void 같은Ip나_같은Id일_경우_조회수가_증가하지_않음() {
        //given
        Long authorId = userFacade.register(userCommand);
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        Post savedPost = userFacade.write(post);
        String userIp = "127.0.0.1";
        String otherIp = "127.0.0.2";

        //when
        userFacade.increaseViewCount(savedPost.getId(), authorId, userIp);
        userFacade.increaseViewCount(savedPost.getId(), 2L, userIp);
        userFacade.increaseViewCount(savedPost.getId(), authorId, otherIp);

        //then
        assertThat(postService.getPostStat(savedPost.getId()).getViewCount()).isEqualTo(1);
    }

    @Test
    void 다른_Ip와_다른Id일_경우_조회수_증가() {
        //given
        Long authorId = userFacade.register(userCommand);
        long boardId = 1L;
        long categoryId = 1L;
        Post post = Post.write(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
        Post savedPost = userFacade.write(post);
        String userIp = "127.0.0.1";
        String otherIp = "127.0.0.2";

        //when
        userFacade.increaseViewCount(savedPost.getId(), authorId, userIp);
        userFacade.increaseViewCount(savedPost.getId(), 2L, otherIp);

        //then
        assertThat(postService.getPostStat(savedPost.getId()).getViewCount()).isEqualTo(2);
    }
}