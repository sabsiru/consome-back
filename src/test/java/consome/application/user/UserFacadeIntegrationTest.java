package consome.application.user;

import consome.domain.point.*;
import consome.domain.user.User;
import consome.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

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
        Long userId = userFacade.create(userCommand);
        int initialPoint = 100;
        int beforePoint = 0;
        int afterPoint = beforePoint+initialPoint;

        // then
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getId()).isEqualTo(userId);
        assertThat(savedUser.getLoginId()).isEqualTo(userCommand.getLoginId());
        assertThat(savedUser.getNickname()).isEqualTo(userCommand.getNickname());

        Optional<PointHistory> history = pointHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

        assertThat(history.get().getUserId()).isEqualTo(userId);
        assertThat(history.get().getBeforePoint()).isEqualTo(beforePoint);
        assertThat(history.get().getAfterPoint()).isEqualTo(afterPoint);
        assertThat(history.get().getType()).isEqualTo(PointHistoryType.SIGNUP);
        assertThat(history.get().getDescription()).contains(PointHistoryType.SIGNUP.getDescription());

        Point userPoint = pointRepository.findByUserId(userId).orElseThrow();
        assertThat(userPoint.getPoint()).isEqualTo(initialPoint);
    }
}