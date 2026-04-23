package consome.interfaces.statistics.dto;

import consome.application.statistics.HourlyActivityResult;
import consome.domain.statistics.ActivityType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public record HourlyActivityResponse(
        int days,
        ActivityType type,
        boolean aggregated,
        List<DayRow> rows
) {
    public record DayRow(LocalDate date, List<Long> hours) {}

    public static HourlyActivityResponse from(HourlyActivityResult result) {
        List<DayRow> rows = result.rows().stream()
                .map(r -> new DayRow(
                        r.date(),
                        Arrays.stream(r.hours()).boxed().toList()
                ))
                .toList();
        return new HourlyActivityResponse(result.days(), result.type(), result.aggregated(), rows);
    }
}
