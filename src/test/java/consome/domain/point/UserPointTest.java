package consome.domain.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserPointTest {

    @Test
    void 유저포인트_초기화_성공_tester() {
        // given
        Long userId = 1L;

        // when
        UserPoint point = UserPoint.initialize(userId);

        // then
        assertThat(point.getUserId()).isEqualTo(userId);
        assertThat(point.getPoint()).isEqualTo(0);
        assertThat(point.getUpdatedAt()).isNotNull();
    }

    @Test
    void 포인트_증가_성공_tester() {
        // given
        UserPoint point = UserPoint.initialize(1L);

        // when
        point.increase(50);

        // then
        assertThat(point.getPoint()).isEqualTo(50);
    }

    @Test
    void 포인트_감소_성공_tester() {
        // given
        UserPoint point = UserPoint.initialize(1L);
        point.increase(100);

        // when
        point.decrease(30);

        // then
        assertThat(point.getPoint()).isEqualTo(70);
    }

}