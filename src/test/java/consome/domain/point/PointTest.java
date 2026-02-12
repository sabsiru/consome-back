package consome.domain.point;

import consome.domain.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PointTest {

    @Test
    void 유저포인트_초기화_성공_tester() {
        // given
        Long userId = 1L;

        // when
        Point point = Point.initialize(userId);

        // then
        assertThat(point.getUserId()).isEqualTo(userId);
        assertThat(point.getUserPoint()).isEqualTo(0);
        assertThat(point.getUpdatedAt()).isNotNull();
    }

    @Test
    void 포인트_증가_성공_tester() {
        // given
        Point point = Point.initialize(1L);

        // when
        point.earn(50);

        // then
        assertThat(point.getUserPoint()).isEqualTo(50);
    }

    @Test
    void 포인트_감소_성공_tester() {
        // given
        Point point = Point.initialize(1L);
        point.earn(100);

        // when
        point.penalize(30);

        // then
        assertThat(point.getUserPoint()).isEqualTo(70);
    }

    @Test
    void 음수_포인트_적립시_BusinessException_발생() {
        // given
        Point point = Point.initialize(1L);

        // when & then
        assertThatThrownBy(() -> point.earn(-10))
                .isInstanceOf(BusinessException.InvalidPointAmount.class)
                .hasMessageContaining("적립");
    }

    @Test
    void 음수_포인트_차감시_BusinessException_발생() {
        // given
        Point point = Point.initialize(1L);

        // when & then
        assertThatThrownBy(() -> point.penalize(-10))
                .isInstanceOf(BusinessException.InvalidPointAmount.class)
                .hasMessageContaining("차감");
    }
}