package consome.domain.point;

import consome.domain.point.repository.PointHistoryRepository;
import consome.domain.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    private Long userId;
    private Point point;

    @BeforeEach
    void setUp() {
        userId = 1L;
        point = Point.initialize(userId);
    }

    @Test
    public void earn_정상적인_포인트_적립() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointRepository.save(any(Point.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int initialPoint = point.getUserPoint();
        int earnAmount = 100;
        PointHistoryType pointHistoryType = PointHistoryType.POST_WRITE;
        // when
        int resultPoint = pointService.earn(userId, pointHistoryType);

        // then
        assertThat(resultPoint).isEqualTo(initialPoint + earnAmount);
        verify(pointRepository).findByUserId(userId);
        verify(pointRepository).save(point);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    public void earn_사용자가_존재하지_않는_경우() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.earn(userId,PointHistoryType.POST_WRITE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

        verify(pointRepository).findByUserId(userId);
        verify(pointRepository, never()).save(any());
        verify(pointHistoryRepository, never()).save(any());
    }

    @Test
    public void penalize_정상적인_포인트_차감() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));
        when(pointRepository.save(any(Point.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int penalizeAmount = 50;
        PointHistoryType pointHistoryType = PointHistoryType.POST_WRITE;
        // when
        int resultPoint = pointService.penalize(userId, pointHistoryType);

        // then
        assertThat(resultPoint).isEqualTo(100 - penalizeAmount);
        verify(pointRepository).findByUserId(userId);
        verify(pointRepository).save(point);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    public void penalize_사용자가_존재하지_않는_경우_IllegalStateException을_반환한다() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.penalize(userId, PointHistoryType.POST_WRITE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

        verify(pointRepository).findByUserId(userId);
        verify(pointRepository, never()).save(any());
        verify(pointHistoryRepository, never()).save(any());
    }

    @Test
    public void initialize_포인트_초기화_성공() {
        // given
        when(pointRepository.save(any(Point.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        int resultPoint = pointService.initialize(userId);

        // then
        assertThat(resultPoint).isEqualTo(100);
        verify(pointRepository).save(any(Point.class));
    }


    @Test
    public void getCurrentPoint_정상적인_포인트_조회() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when
        int resultPoint = pointService.getCurrentPoint(userId);

        // then
        assertThat(resultPoint).isEqualTo(100); // 초기값 100 확인
        verify(pointRepository).findByUserId(userId);
    }

    @Test
    public void getCurrentPoint_사용자가_존재하지_않는_경우_IllegalStateException을_반환한다() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.getCurrentPoint(userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

        verify(pointRepository).findByUserId(userId);
    }

    @Test
    public void findPointByUserId_사용자_조회_성공() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(point));

        // when
        Point result = pointService.findPointByUserId(userId);

        // then
        assertThat(result).isEqualTo(point);
        verify(pointRepository).findByUserId(userId);
    }

    @Test
    public void findPointByUserId_사용자가_존재하지_않는_경우_IllegalStateException을_반환한다() {
        // given
        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.findPointByUserId(userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

        verify(pointRepository).findByUserId(userId);
    }
}