package consome.application.user;

import consome.domain.comment.Comment;
import consome.domain.comment.CommentReactionRepository;
import consome.domain.comment.CommentRepository;
import consome.domain.comment.CommentService;
import consome.domain.point.*;
import consome.domain.post.entity.Post;
import consome.domain.post.PostService;
import consome.domain.post.entity.PostStat;
import consome.domain.post.ReactionType;
import consome.domain.user.User;
import consome.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        userRegisterCommand = UserRegisterCommand.of("testid", "테스트닉네임", "password123");
        userRepository.deleteAll();
    }

    @Test
    void 회원가입_요청시_사용자가_생성되고_포인트가_초기화되며_히스토리가_생성된다() {
        // when
        Long userId = userFacade.register(userRegisterCommand);
        int initialPoint = PointHistoryType.REGISTER.getPoint();
        int beforePoint = 0;
        int afterPoint = beforePoint + initialPoint;

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getId()).isEqualTo(userId);
        assertThat(savedUser.getLoginId()).isEqualTo(userRegisterCommand.getLoginId());
        assertThat(savedUser.getNickname()).isEqualTo(userRegisterCommand.getNickname());

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
        void 유저_loginId가_중복일시_예외발생 () {
            //given
            userFacade.register(userRegisterCommand);
            UserRegisterCommand duplicateUserRegisterCommand = UserRegisterCommand.of("testid", "다른닉네임", "다른비밀번호");

            //then
            assertThatThrownBy(() -> userFacade.register(duplicateUserRegisterCommand))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 사용 중인 아이디입니다.");
        }

        @Test
        void 유저_닉네임이_중복일시_예외발생 () {
            //given
            userFacade.register(userRegisterCommand);
            UserRegisterCommand duplicateUserRegisterCommand = UserRegisterCommand.of("다른아이디", "테스트닉네임", "다른비밀번호");

            //then
            assertThatThrownBy(() -> userFacade.register(duplicateUserRegisterCommand))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 사용 중인 닉네임입니다.");

        }

        @Test
        void 게시글_작성후_포인트조회 () {
            // given
            Long userId = userFacade.register(userRegisterCommand);
            int initialPoint = PointHistoryType.REGISTER.getPoint();
            int postPoint = PointHistoryType.POST_WRITE.getPoint();
            int expectedPoint = initialPoint + postPoint;
            // when
            userFacade.post(boardId, categoryId, userId, "테스트 제목", "테스트 내용");

            // then
            Point point = pointRepository.findByUserId(userId).orElseThrow();
            assertThat(point.getPoint()).isEqualTo(expectedPoint);
        }

        @Test
        void 게시글_정상_수정_확인 () {
            // given
            Long userId = userFacade.register(userRegisterCommand);
            Post post = userFacade.post(boardId, categoryId, userId, "테스트 제목", "테스트 내용");

            // when
            Post edited = userFacade.editPost("수정된 제목", "수정된 내용", post.getId(), userId);

            // then
            assertThat(post.getTitle()).isEqualTo(edited.getTitle());
            assertThat(post.getContent()).isEqualTo(edited.getContent());
        }

        @Test
        void 게시글_삭제시_포인트_차감_확인 () {
            //given
            Long userId = userFacade.register(userRegisterCommand);
            Post post = userFacade.post(boardId, categoryId, userId, "테스트 제목", "테스트 내용");

            //when
            userFacade.deletePost(post.getId(), userId);

            //then
            Point point = pointRepository.findByUserId(userId).orElseThrow();
            assertThat(point.getPoint()).isEqualTo(100);
        }

        @Test
        void 게시글_작성후_다른유저로_좋아요시_포인트증가_확인 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long otherUser = userFacade.register(UserRegisterCommand.of("likerid", "좋아요닉네임", "likerpassword"));
            int initialPoint = PointHistoryType.REGISTER.getPoint();
            int postPoint = PointHistoryType.POST_WRITE.getPoint();
            int likePoint = PointHistoryType.POST_LIKE.getPoint();
            int expectedPoint = initialPoint + postPoint + likePoint;
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            //when

            userFacade.likePost(post, otherUser);

            //then
            Point authorPoint = pointRepository.findByUserId(authorId).orElseThrow();
            assertThat(authorPoint.getPoint()).isEqualTo(expectedPoint);
        }

        @Test
        void 게시글_좋아요_중복시_예외발생 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long otherUser = userFacade.register(UserRegisterCommand.of("likerid", "좋아요닉네임", "likerpassword"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //when
            userFacade.likePost(post, otherUser);

            //then
            assertThatThrownBy(() -> userFacade.likePost(post, otherUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 좋아요를 누른 게시글입니다.");

        }

        @Test
        void 게시글_싫어요_정상차감_확인 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long otherUser = userFacade.register(UserRegisterCommand.of("dislikerid", "싫어요닉네임", "dislikerpassword"));
            int initialPoint = PointHistoryType.REGISTER.getPoint();
            int postPoint = PointHistoryType.POST_WRITE.getPoint();
            int dislikePoint = PointHistoryType.POST_DISLIKE.getPoint();
            int expectedPoint = initialPoint + postPoint - dislikePoint;
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //when
            userFacade.dislikePost(post, otherUser);

            //then
            Point authorPoint = pointRepository.findByUserId(authorId).orElseThrow();
            assertThat(authorPoint.getPoint()).isEqualTo(expectedPoint);
        }

        @Test
        void 게시글_싫어요_중복시_예외발생 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long otherUser = userFacade.register(UserRegisterCommand.of("dislikerid", "싫어요닉네임", "dislikerpassword"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //when
            userFacade.dislikePost(post, otherUser);

            //then
            assertThatThrownBy(() -> userFacade.dislikePost(post, otherUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 싫어요를 누른 게시글입니다.");
        }

        @Test
        void 게시글_수정시_작성자가_아니면_예외발생 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long otherUser = userFacade.register(UserRegisterCommand.of("otherid", "다른닉네임", "otherpassword"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //when

            //then
            assertThatThrownBy(() -> userFacade.editPost("수정된 제목", "수정된 내용", post.getId(), otherUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("작성자만 게시글을 수정할 수 있습니다.");
        }

        @Test
        void 게시글_삭제시_작성자가_아니면_예외발생 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long otherUser = userFacade.register(UserRegisterCommand.of("otherid", "다른닉네임", "otherpassword"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //then
            assertThatThrownBy(() -> userFacade.deletePost(post.getId(), otherUser))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("작성자만 게시글을 삭제할 수 있습니다.");
        }

        @Test
        void 게시글_조회수_증가_확인 () {
            // given
            Long authorId = userFacade.register(userRegisterCommand);
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            String userIp = "127.0.0.1";

            // when
            userFacade.increaseViewCount(post.getId(), userIp, authorId);

            // then
            PostStat postStat = postService.getPostStat(post.getId());
            assertThat(postStat.getViewCount()).isEqualTo(1);
        }

        @Test
        void 같은Ip나_같은Id일_경우_조회수가_증가하지_않음 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            String userIp = "127.0.0.1";
            String otherIp = "127.0.0.2";

            //when
            userFacade.increaseViewCount(post.getId(), userIp, authorId);
            userFacade.increaseViewCount(post.getId(), userIp, 2L);
            userFacade.increaseViewCount(post.getId(), otherIp, authorId);

            //then
            assertThat(postService.getPostStat(post.getId()).getViewCount()).isEqualTo(1);
        }

        @Test
        void 다른_Ip와_다른Id일_경우_조회수_증가 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            String userIp = "127.0.0.1";
            String otherIp = "127.0.0.2";

            //when
            userFacade.increaseViewCount(post.getId(), userIp, authorId);
            userFacade.increaseViewCount(post.getId(), otherIp, 2L);

            //then
            assertThat(postService.getPostStat(post.getId()).getViewCount()).isEqualTo(2);
        }

        /*
         * 포인트 검증도 전부 추가 해야함.
         * */

        @Test
        void 댓글_정상_작성 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //when
            userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");

            //then
            assertThat(commentRepository.findByPostIdOrderByRefAscStepAsc(post.getId())).hasSize(1);
        }

        @Test
        void 대댓글_정상_작성 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");

            //when
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");
            userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용2");
            userFacade.comment(post.getId(), commenterId, comment.getId(), "테스트 대댓글 내용");

            List<Comment> comments = commentRepository.findByPostIdOrderByRefAscStepAsc(post.getId());
            //then
            // 댓글 대댓글 댓글 순서 확인
            assertThat(commentRepository.findByPostIdOrderByRefAscStepAsc(post.getId())).hasSize(3);
            assertThat(comments.get(0).getContent()).isEqualTo("테스트 댓글 내용");
            assertThat(comments.get(1).getContent()).isEqualTo("테스트 대댓글 내용");
            assertThat(comments.get(2).getContent()).isEqualTo("테스트 댓글 내용2");

        }

        @Test
        void 댓글_수정_테스트 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            String newContent = "수정된 댓글 내용";
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");

            //when
            Comment editComment = userFacade.editComment(commenterId, comment.getId(), newContent);

            //then
            assertThat(editComment.getContent()).isEqualTo(newContent);
        }

        @Test
        void 댓글_삭제_테스트 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");
            userFacade.deleteComment(commenterId, comment.getId());

            //when&then
            assertThat(comment.getContent()).isEqualTo("삭제된 댓글입니다.");
        }

        @Test
        void 다른_유저의_댓글_수정시_예외발생 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Long otherUserId = 100L;
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");

            //when&then
            assertThatThrownBy(() -> userFacade.editComment(otherUserId, comment.getId(), "수정된 댓글 내용"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("작성자만 댓글을 수정할 수 있습니다.");
        }

        @Test
        void 다른_유저의_댓글_삭제시_예외발생 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Long otherUserId = 100L;
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");

            //when&then
            assertThatThrownBy(() -> userFacade.deleteComment(otherUserId, comment.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("작성자만 댓글을 삭제할 수 있습니다.");
        }

        @Test
        void 댓글_좋아요_성공 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Long likerId = userFacade.register(UserRegisterCommand.of("likerid", "좋아요닉네임", "likerpassword"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");

            //when
            userFacade.likeComment(comment.getId(), likerId);
            long likeCount = commentService.countReactions(comment.getId(), ReactionType.LIKE);

            //then
            assertThat(likeCount).isEqualTo(1);
        }

        @Test
        void 댓글_싫어요_성공 () {
            //given
            Long authorId = userFacade.register(userRegisterCommand);
            Long commenterId = userFacade.register(UserRegisterCommand.of("commenterid", "댓글작성자", "1234"));
            Long dislikerId = userFacade.register(UserRegisterCommand.of("dislikerid", "싫어요닉네임", "dislikerpassword"));
            Post post = userFacade.post(boardId, categoryId, authorId, "테스트 제목", "테스트 내용");
            Comment comment = userFacade.comment(post.getId(), commenterId, null, "테스트 댓글 내용");

            //when
            userFacade.dislikeComment(comment.getId(), dislikerId);
            long dislikeCount = commentService.countReactions(comment.getId(), ReactionType.DISLIKE);

            //then
            assertThat(dislikeCount).isEqualTo(1);
        }
    }