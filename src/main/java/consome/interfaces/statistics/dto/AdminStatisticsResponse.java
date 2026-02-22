package consome.interfaces.statistics.dto;

import consome.application.statistics.AdminStatisticsResult;

public record AdminStatisticsResponse(
        long totalVisitors,
        long todayVisitors,
        int onlineCount
) {
    public static AdminStatisticsResponse from(AdminStatisticsResult result) {
        return new AdminStatisticsResponse(
                result.totalVisitors(),
                result.todayVisitors(),
                result.onlineCount()
        );
    }
}
