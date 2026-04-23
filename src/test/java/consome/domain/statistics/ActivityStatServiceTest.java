package consome.domain.statistics;

import consome.infrastructure.redis.HourlyActivityRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivityStatServiceTest {

    @Mock
    private HourlyActivityRedisRepository hourlyActivityRedisRepository;

    @InjectMocks
    private ActivityStatService activityStatService;

    @Test
    void recordActivity_POST_호출시_POST_증분() {
        activityStatService.recordActivity(ActivityType.POST);
        verify(hourlyActivityRedisRepository).increment(ActivityType.POST);
    }

    @Test
    void recordActivity_COMMENT_호출시_COMMENT_증분() {
        activityStatService.recordActivity(ActivityType.COMMENT);
        verify(hourlyActivityRedisRepository).increment(ActivityType.COMMENT);
    }

    @Test
    void recordActivity_LIKE_DISLIKE_VISIT_각각_증분() {
        activityStatService.recordActivity(ActivityType.LIKE);
        activityStatService.recordActivity(ActivityType.DISLIKE);
        activityStatService.recordActivity(ActivityType.VISIT);

        verify(hourlyActivityRedisRepository).increment(ActivityType.LIKE);
        verify(hourlyActivityRedisRepository).increment(ActivityType.DISLIKE);
        verify(hourlyActivityRedisRepository).increment(ActivityType.VISIT);
    }

    @Test
    void recordActivity_null이면_skip() {
        activityStatService.recordActivity(null);
        verify(hourlyActivityRedisRepository, never()).increment(null);
    }

    @Test
    void recordVisit_VISIT_증분_별칭() {
        activityStatService.recordVisit();
        verify(hourlyActivityRedisRepository).increment(ActivityType.VISIT);
    }
}
