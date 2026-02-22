package consome.domain.statistics;

import consome.domain.statistics.repository.SiteVisitRepository;
import consome.infrastructure.redis.OnlineUserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final SiteVisitRepository siteVisitRepository;
    private final OnlineUserRedisRepository onlineUserRedisRepository;

    public long getTotalVisitors() {
        return siteVisitRepository.countTotalUniqueVisitors();
    }

    public long getTodayVisitors() {
        return siteVisitRepository.countByVisitDate(LocalDate.now());
    }

    public int getOnlineCount() {
        return onlineUserRedisRepository.getOnlineCount();
    }
}
