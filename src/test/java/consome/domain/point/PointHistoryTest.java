package consome.domain.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class PointHistoryTest {
    @Test
    void 포인트_획득_기록_생성_tester() {
        // given
        Long userId = 1L;
        int amount = 30;
        String reason = "게시글 작성";
        int pointAfter = 80;

        // when
        PointHistory history = PointHistory.earn(userId, amount, reason, pointAfter);

        // then
        assertThat(history.getUserId()).isEqualTo(userId);
        assertThat(history.getAmount()).isEqualTo(amount);
        assertThat(history.getType()).isEqualTo(PointHistoryType.EARN);
        assertThat(history.getReason()).isEqualTo(reason);
        assertThat(history.getCurrentPoint()).isEqualTo(pointAfter);
        assertThat(history.getCreatedAt()).isNotNull();
    }

    @Test
    void 포인트_벌점_기록_생성_tester() {
        PointHistory history = PointHistory.penalize(3L, 10, "비추천 누적", 15);
        assertThat(history.getType()).isEqualTo(PointHistoryType.PENALIZE);
    }
}
