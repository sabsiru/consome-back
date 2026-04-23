package consome.application.statistics;

import consome.domain.statistics.ActivityType;

import java.time.LocalDate;
import java.util.List;

/**
 * 시간대별 활동량 응답
 * - days: 7 일 단위
 * - type: 단일 타입(POST/COMMENT/...)이거나 ALL
 * - rows: 날짜별 24시간 카운트 행
 */
public record HourlyActivityResult(
        int days,
        ActivityType type,
        boolean aggregated,
        List<DayRow> rows
) {
    public record DayRow(LocalDate date, long[] hours) {}
}
