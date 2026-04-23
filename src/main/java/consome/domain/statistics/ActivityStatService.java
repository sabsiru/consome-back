package consome.domain.statistics;

import consome.infrastructure.redis.HourlyActivityRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityStatService {

    private final HourlyActivityRedisRepository hourlyActivityRedisRepository;

    public void recordActivity(ActivityType type) {
        if (type == null) return;
        hourlyActivityRedisRepository.increment(type);
    }

    public void recordVisit() {
        recordActivity(ActivityType.VISIT);
    }
}
