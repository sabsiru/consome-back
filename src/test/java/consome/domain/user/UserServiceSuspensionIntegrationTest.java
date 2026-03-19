package consome.domain.user;

import consome.domain.user.repository.SuspensionHistoryRepository;
import consome.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
@ActiveProfiles("test")
class UserServiceSuspensionIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SuspensionHistoryRepository suspensionHistoryRepository;

    private User createUser(String loginId, String nickname, String email) {
        User user = User.create(loginId, nickname, "Password123", email);
        return userRepository.save(user);
    }

    @Test
    void 만료된_제재_유저를_일괄_해제한다() {
        // given
        User user1 = createUser("expired1", "만료유저1", "expired1@test.com");
        User user2 = createUser("expired2", "만료유저2", "expired2@test.com");
        User activeUser = createUser("active1", "활성유저", "active@test.com");
        User permanentUser = createUser("perm1", "영구유저", "perm@test.com");

        // 1일 제재 → 이미 만료됨 (suspendedUntil을 과거로 설정하기 위해 직접 suspend 후 시간 조작)
        userService.suspend(user1.getId(), SuspensionType.DAY_1, "테스트 제재1", null, 999L);
        userService.suspend(user2.getId(), SuspensionType.DAY_1, "테스트 제재2", null, 999L);
        userService.suspend(permanentUser.getId(), SuspensionType.PERMANENT, "영구 정지", null, 999L);

        // suspendedUntil을 과거로 직접 설정 (만료 시뮬레이션)
        user1 = userRepository.findById(user1.getId()).orElseThrow();
        user2 = userRepository.findById(user2.getId()).orElseThrow();
        ReflectionTestUtils.setField(user1, "suspendedUntil", LocalDateTime.now().minusHours(1));
        ReflectionTestUtils.setField(user2, "suspendedUntil", LocalDateTime.now().minusDays(1));
        userRepository.flush();

        // when
        int cleanedCount = userService.cleanupExpiredSuspensions();

        // then
        assertThat(cleanedCount).isEqualTo(2);

        User reloaded1 = userRepository.findById(user1.getId()).orElseThrow();
        User reloaded2 = userRepository.findById(user2.getId()).orElseThrow();
        User reloadedPerm = userRepository.findById(permanentUser.getId()).orElseThrow();
        User reloadedActive = userRepository.findById(activeUser.getId()).orElseThrow();

        // 만료된 유저는 unsuspend 됨
        assertThat(reloaded1.getSuspensionType()).isNull();
        assertThat(reloaded1.getSuspendedUntil()).isNull();
        assertThat(reloaded1.getSuspendReason()).isNull();

        assertThat(reloaded2.getSuspensionType()).isNull();

        // 영구 정지 유저는 그대로
        assertThat(reloadedPerm.getSuspensionType()).isEqualTo(SuspensionType.PERMANENT);

        // 제재 안 받은 유저는 그대로
        assertThat(reloadedActive.getSuspensionType()).isNull();
    }

    @Test
    void 정지중인_유저에게_재정지시_현재시점_기준으로_계산된다() {
        // given
        User user = createUser("sus1", "정지유저", "sus1@test.com");
        userService.suspend(user.getId(), SuspensionType.DAY_1, "첫 번째 정지", null, 999L);

        // suspendedUntil을 3일 뒤로 설정 (정지 중인 상태)
        user = userRepository.findById(user.getId()).orElseThrow();
        ReflectionTestUtils.setField(user, "suspendedUntil", LocalDateTime.now().plusDays(3));
        userRepository.flush();

        // when - 새 1일 정지 적용
        LocalDateTime beforeSuspend = LocalDateTime.now();
        userService.suspend(user.getId(), SuspensionType.DAY_1, "두 번째 정지", null, 999L);
        LocalDateTime afterSuspend = LocalDateTime.now();

        // then - User의 suspendedUntil은 now() + 1일
        User reloaded = userRepository.findById(user.getId()).orElseThrow();
        assertThat(reloaded.getSuspendedUntil()).isAfterOrEqualTo(beforeSuspend.plusDays(1));
        assertThat(reloaded.getSuspendedUntil()).isBeforeOrEqualTo(afterSuspend.plusDays(1));

        // SuspensionHistory의 endAt도 startAt + 1일
        List<SuspensionHistory> histories = suspensionHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        SuspensionHistory latestHistory = histories.get(0);
        assertThat(latestHistory.getEndAt()).isAfterOrEqualTo(beforeSuspend.plusDays(1));
        assertThat(latestHistory.getEndAt()).isBeforeOrEqualTo(afterSuspend.plusDays(1));
    }

    @Test
    void 만료된_제재가_없으면_0을_반환한다() {
        // given
        User user = createUser("noexpire", "미만료유저", "noexpire@test.com");
        userService.suspend(user.getId(), SuspensionType.DAY_30, "30일 제재", null, 999L);

        // when
        int cleanedCount = userService.cleanupExpiredSuspensions();

        // then
        assertThat(cleanedCount).isEqualTo(0);
    }
}
