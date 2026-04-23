package consome.application.statistics;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.statistics.ActivityType;
import consome.domain.statistics.StatisticsService;
import consome.domain.statistics.entity.HourlyActivity;
import consome.domain.statistics.repository.HourlyActivityRepository;
import consome.infrastructure.redis.HourlyActivityRedisRepository;
import consome.infrastructure.redis.SearchKeywordRedisRepository;
import consome.infrastructure.redis.SearchKeywordRedisRepository.KeywordScore;
import consome.infrastructure.redis.VisitedBoardRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsFacade {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int MAX_KEYWORD_LIMIT = 50;

    private final StatisticsService statisticsService;
    private final VisitedBoardRedisRepository visitedBoardRedisRepository;
    private final BoardService boardService;
    private final SearchKeywordRedisRepository searchKeywordRedisRepository;
    private final HourlyActivityRedisRepository hourlyActivityRedisRepository;
    private final HourlyActivityRepository hourlyActivityRepository;

    public AdminStatisticsResult getAdminStatistics() {
        return new AdminStatisticsResult(
                statisticsService.getTotalVisitors(),
                statisticsService.getTodayVisitors(),
                statisticsService.getOnlineCount()
        );
    }

    public int getOnlineCount() {
        return statisticsService.getOnlineCount();
    }

    public List<VisitedBoardResult> getVisitedBoards(Long userId) {
        List<Long> boardIds = visitedBoardRedisRepository.getRecentVisitedBoardIds(userId);
        if (boardIds.isEmpty()) {
            return List.of();
        }

        return boardIds.stream()
                .map(boardId -> {
                    Board board = boardService.findById(boardId);
                    return new VisitedBoardResult(board.getId(), board.getName());
                })
                .toList();
    }

    public void recordBoardVisit(Long userId, Long boardId) {
        if (userId != null) {
            visitedBoardRedisRepository.recordVisit(userId, boardId);
        }
    }

    public List<PopularKeywordResult> getPopularKeywords(String period, int limit) {
        String normalizedPeriod = "hour".equals(period) ? "hour" : "day";
        int normalizedLimit = Math.min(Math.max(limit, 1), MAX_KEYWORD_LIMIT);

        List<KeywordScore> scores = searchKeywordRedisRepository.getTopKeywords(normalizedPeriod, normalizedLimit);

        List<PopularKeywordResult> result = new ArrayList<>(scores.size());
        int rank = 1;
        for (KeywordScore score : scores) {
            result.add(new PopularKeywordResult(rank++, score.keyword(), score.count()));
        }
        return result;
    }

    public HourlyActivityResult getHourlyActivity(int days, String typeStr) {
        int normalizedDays = Math.min(Math.max(days, 1), 30);
        boolean aggregated = typeStr == null || "ALL".equalsIgnoreCase(typeStr);
        ActivityType singleType = aggregated ? null : ActivityType.valueOf(typeStr.toUpperCase());

        LocalDateTime now = LocalDateTime.now(KST);
        LocalDate today = now.toLocalDate();
        int currentHour = now.getHour();
        LocalDate startDate = today.minusDays(normalizedDays - 1L);

        // DB 누적 (이미 flush된 데이터)
        long[][] matrix = new long[normalizedDays][24];
        List<HourlyActivity> rows = aggregated
                ? hourlyActivityRepository.findAllByDateRange(startDate, today)
                : hourlyActivityRepository.findAllByDateRangeAndType(startDate, today, singleType);
        for (HourlyActivity row : rows) {
            int dayIdx = (int) (row.getActivityDate().toEpochDay() - startDate.toEpochDay());
            if (dayIdx < 0 || dayIdx >= normalizedDays) continue;
            int h = row.getHour();
            if (h < 0 || h >= 24) continue;
            matrix[dayIdx][h] += row.getCount();
        }

        // 미flush 보정: 오늘 currentHour 데이터를 Redis에서 조회
        int todayIdx = normalizedDays - 1;
        if (aggregated) {
            for (ActivityType t : ActivityType.values()) {
                matrix[todayIdx][currentHour] += hourlyActivityRedisRepository.getCount(t, today, currentHour);
            }
        } else {
            matrix[todayIdx][currentHour] += hourlyActivityRedisRepository.getCount(singleType, today, currentHour);
        }

        List<HourlyActivityResult.DayRow> dayRows = new ArrayList<>(normalizedDays);
        for (int i = 0; i < normalizedDays; i++) {
            dayRows.add(new HourlyActivityResult.DayRow(startDate.plusDays(i), matrix[i]));
        }
        return new HourlyActivityResult(normalizedDays, singleType, aggregated, dayRows);
    }
}
