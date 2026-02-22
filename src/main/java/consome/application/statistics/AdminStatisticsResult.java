package consome.application.statistics;

public record AdminStatisticsResult(
        long totalVisitors,
        long todayVisitors,
        int onlineCount
) {}
