package consome.domain.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class PointTest {

    @Test
    void 유저포인트_초기화_성공_tester() {
        // given
        Long userId = 1L;

        // when
        Point point = Point.initialize(userId);

        // then
        assertThat(point.getRefUserId()).isEqualTo(userId);
        assertThat(point.getPoint()).isEqualTo(0);
        assertThat(point.getUpdatedAt()).isNotNull();
    }

    @Test
    void 포인트_증가_성공_tester() {
        // given
        Point point = Point.initialize(1L);

        // when
        point.earn(50);

        // then
        assertThat(point.getPoint()).isEqualTo(50);
    }

    @Test
    void 포인트_감소_성공_tester() {
        // given
        Point point = Point.initialize(1L);
        point.earn(100);

        // when
        point.penalize(30);

        // then
        assertThat(point.getPoint()).isEqualTo(70);
    }

}