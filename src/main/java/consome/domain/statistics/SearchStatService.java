package consome.domain.statistics;

import consome.infrastructure.redis.SearchKeywordRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SearchStatService {

    private static final int MAX_KEYWORD_LENGTH = 30;

    private final SearchKeywordRedisRepository searchKeywordRedisRepository;

    /**
     * 검색 키워드 카운트
     * - 정규화: trim + toLowerCase
     * - 빈 값 / 30자 초과 → skip
     * - dedupe 60s: (user:userId | ip:ip) + normalizedKeyword
     */
    public void recordKeyword(String keyword, Long userId, String ip) {
        if (keyword == null) return;
        String normalized = keyword.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || normalized.length() > MAX_KEYWORD_LENGTH) return;

        String requesterKey = userId != null ? "user:" + userId : "ip:" + ip;
        if (!searchKeywordRedisRepository.markIfAbsent(requesterKey, normalized)) return;

        searchKeywordRedisRepository.increment(normalized);
    }
}
