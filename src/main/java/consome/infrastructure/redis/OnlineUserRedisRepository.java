package consome.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OnlineUserRedisRepository {

    private static final String KEY = "online:users";
    private static final long ONLINE_THRESHOLD_SECONDS = 300; // 5분

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 사용자 활동 기록 (5분 내 활동 시 온라인으로 간주)
     */
    public void recordActivity(String memberKey) {
        try {
            double score = Instant.now().getEpochSecond();
            redisTemplate.opsForZSet().add(KEY, memberKey, score);
        } catch (Exception e) {
            log.warn("Failed to record online activity: {}", e.getMessage());
        }
    }

    /**
     * 현재 접속자 수 조회 (5분 이내 활동자)
     */
    public int getOnlineCount() {
        try {
            cleanupExpired();
            Long count = redisTemplate.opsForZSet().zCard(KEY);
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            log.warn("Failed to get online count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 만료된 항목 정리 (5분 이상 비활동)
     */
    private void cleanupExpired() {
        try {
            long threshold = Instant.now().getEpochSecond() - ONLINE_THRESHOLD_SECONDS;
            redisTemplate.opsForZSet().removeRangeByScore(KEY, 0, threshold);
        } catch (Exception e) {
            log.warn("Failed to cleanup expired entries: {}", e.getMessage());
        }
    }

    public static String buildMemberKey(Long userId, String ip) {
        return userId != null ? "user:" + userId : "ip:" + ip;
    }
}
