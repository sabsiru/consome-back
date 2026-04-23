package consome.application.statistics;

import consome.domain.admin.BoardService;
import consome.domain.statistics.ActivityType;
import consome.domain.statistics.StatisticsService;
import consome.domain.statistics.entity.HourlyActivity;
import consome.domain.statistics.repository.HourlyActivityRepository;
import consome.infrastructure.redis.HourlyActivityRedisRepository;
import consome.infrastructure.redis.SearchKeywordRedisRepository;
import consome.infrastructure.redis.SearchKeywordRedisRepository.KeywordScore;
import consome.infrastructure.redis.VisitedBoardRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsFacadeTest {

    @Mock private StatisticsService statisticsService;
    @Mock private VisitedBoardRedisRepository visitedBoardRedisRepository;
    @Mock private BoardService boardService;
    @Mock private SearchKeywordRedisRepository searchKeywordRedisRepository;
    @Mock private HourlyActivityRedisRepository hourlyActivityRedisRepository;
    @Mock private HourlyActivityRepository hourlyActivityRepository;

    @InjectMocks private StatisticsFacade statisticsFacade;

    @Test
    void getPopularKeywords_TOP_N_조회_rank_부여() {
        when(searchKeywordRedisRepository.getTopKeywords("hour", 10))
                .thenReturn(List.of(
                        new KeywordScore("java", 50L),
                        new KeywordScore("python", 30L),
                        new KeywordScore("vue", 10L)
                ));

        List<PopularKeywordResult> result = statisticsFacade.getPopularKeywords("hour", 10);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).keyword()).isEqualTo("java");
        assertThat(result.get(0).count()).isEqualTo(50L);
        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(2).rank()).isEqualTo(3);
    }

    @Test
    void getPopularKeywords_빈_결과_빈_목록_반환() {
        when(searchKeywordRedisRepository.getTopKeywords(anyString(), anyInt()))
                .thenReturn(List.of());

        List<PopularKeywordResult> result = statisticsFacade.getPopularKeywords("day", 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getHourlyActivity_단일_타입_조회_DB_누적_Redis_현재시간_합산() {
        ActivityType type = ActivityType.POST;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        when(hourlyActivityRepository.findAllByDateRangeAndType(any(), any(), any()))
                .thenReturn(List.of(
                        HourlyActivity.of(yesterday, 10, type, 5L),
                        HourlyActivity.of(today, 9, type, 3L)
                ));
        when(hourlyActivityRedisRepository.getCount(any(), any(), anyInt()))
                .thenReturn(0L);

        HourlyActivityResult result = statisticsFacade.getHourlyActivity(7, "POST");

        assertThat(result.days()).isEqualTo(7);
        assertThat(result.type()).isEqualTo(ActivityType.POST);
        assertThat(result.aggregated()).isFalse();
        assertThat(result.rows()).hasSize(7);
        assertThat(result.rows().get(0).date()).isEqualTo(today.minusDays(6));
        assertThat(result.rows().get(6).date()).isEqualTo(today);

        // yesterday는 today 직전 인덱스
        long[] yesterdayHours = result.rows().get(5).hours();
        assertThat(yesterdayHours).hasSize(24);
        assertThat(yesterdayHours[10]).isEqualTo(5L);

        long[] todayHours = result.rows().get(6).hours();
        assertThat(todayHours[9]).isEqualTo(3L);
    }

    @Test
    void getHourlyActivity_ALL_타입은_aggregated_true_모든_타입_합산() {
        LocalDate today = LocalDate.now();

        when(hourlyActivityRepository.findAllByDateRange(any(), any()))
                .thenReturn(List.of(
                        HourlyActivity.of(today, 5, ActivityType.POST, 2L),
                        HourlyActivity.of(today, 5, ActivityType.COMMENT, 3L),
                        HourlyActivity.of(today, 5, ActivityType.VISIT, 100L)
                ));
        when(hourlyActivityRedisRepository.getCount(any(), any(), anyInt()))
                .thenReturn(0L);

        HourlyActivityResult result = statisticsFacade.getHourlyActivity(1, "ALL");

        assertThat(result.aggregated()).isTrue();
        assertThat(result.type()).isNull();
        assertThat(result.rows()).hasSize(1);
        assertThat(result.rows().get(0).hours()[5]).isEqualTo(105L);
    }

    @Test
    void getHourlyActivity_Redis_현재시간_미flush_데이터_합산() {
        ActivityType type = ActivityType.POST;
        LocalDate today = LocalDate.now();
        int currentHour = java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")).getHour();

        when(hourlyActivityRepository.findAllByDateRangeAndType(any(), any(), any()))
                .thenReturn(List.of());
        when(hourlyActivityRedisRepository.getCount(any(), any(), anyInt()))
                .thenReturn(0L);
        when(hourlyActivityRedisRepository.getCount(type, today, currentHour))
                .thenReturn(7L);

        HourlyActivityResult result = statisticsFacade.getHourlyActivity(1, "POST");

        assertThat(result.rows().get(0).hours()[currentHour]).isEqualTo(7L);
    }
}
