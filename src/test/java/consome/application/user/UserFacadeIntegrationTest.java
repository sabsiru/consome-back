package consome.application.user;

import consome.domain.comment.repository.CommentReactionRepository;
import consome.domain.comment.repository.CommentRepository;
import consome.domain.comment.CommentService;
import consome.domain.point.*;
import consome.domain.post.PostService;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
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
        assertThat(userPoint.getUserPoint()).isEqualTo(initialPoint);
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
    }