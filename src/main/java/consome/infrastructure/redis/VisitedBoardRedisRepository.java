package consome.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VisitedBoardRedisRepository {

    private static final String KEY_PREFIX = "visited:boards:";
    private static final int MAX_SIZE = 10;
    private static final long TTL_DAYS = 7;

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 게시판 방문 기록
     */
    public void recordVisit(Long userId, Long boardId) {
        try {
            String key = KEY_PREFIX + userId;
            String boardIdStr = boardId.toString();

            // 기존 모든 동일 항목 제거 (count=0: 모두 제거)
            Long removed = redisTemplate.opsForList().remove(key, 0, boardIdStr);

            // 맨 앞에 추가
            redisTemplate.opsForList().leftPush(key, boardIdStr);
            redisTemplate.opsForList().trim(key, 0, MAX_SIZE - 1);
            redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Failed to record board visit: {}", e.getMessage());
        }
    }

    /**
     * 최근 방문 게시판 ID 목록 조회 (중복 제거)
     */
    public List<Long> getRecentVisitedBoardIds(Long userId) {
        try {
            String key = KEY_PREFIX + userId;
            List<String> ids = redisTemplate.opsForList().range(key, 0, MAX_SIZE - 1);
            if (ids == null || ids.isEmpty()) {
                return Collections.emptyList();
            }
            // 중복 제거하면서 순서 유지
            return ids.stream()
                    .distinct()
                    .map(Long::parseLong)
                    .limit(MAX_SIZE)
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to get recent visited boards: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
