package consome.domain.statistics;

import consome.infrastructure.redis.SearchKeywordRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchStatServiceTest {

    @Mock
    private SearchKeywordRedisRepository searchKeywordRedisRepository;

    @InjectMocks
    private SearchStatService searchStatService;

    @Test
    void recordKeyword_정상_키워드_정규화후_카운트() {
        when(searchKeywordRedisRepository.markIfAbsent(anyString(), anyString())).thenReturn(true);

        searchStatService.recordKeyword("  Hello World  ", 1L, "1.2.3.4");

        verify(searchKeywordRedisRepository).markIfAbsent("user:1", "hello world");
        verify(searchKeywordRedisRepository).increment("hello world");
    }

    @Test
    void recordKeyword_userId_null이면_ip로_dedupe_키_구성() {
        when(searchKeywordRedisRepository.markIfAbsent(anyString(), anyString())).thenReturn(true);

        searchStatService.recordKeyword("java", null, "1.2.3.4");

        verify(searchKeywordRedisRepository).markIfAbsent("ip:1.2.3.4", "java");
        verify(searchKeywordRedisRepository).increment("java");
    }

    @Test
    void recordKeyword_빈_값_skip() {
        searchStatService.recordKeyword("   ", 1L, "1.2.3.4");

        verify(searchKeywordRedisRepository, never()).markIfAbsent(anyString(), anyString());
        verify(searchKeywordRedisRepository, never()).increment(anyString());
    }

    @Test
    void recordKeyword_null_skip() {
        searchStatService.recordKeyword(null, 1L, "1.2.3.4");

        verify(searchKeywordRedisRepository, never()).markIfAbsent(anyString(), anyString());
        verify(searchKeywordRedisRepository, never()).increment(anyString());
    }

    @Test
    void recordKeyword_30자_초과_skip() {
        String over = "a".repeat(31);

        searchStatService.recordKeyword(over, 1L, "1.2.3.4");

        verify(searchKeywordRedisRepository, never()).markIfAbsent(anyString(), anyString());
        verify(searchKeywordRedisRepository, never()).increment(anyString());
    }

    @Test
    void recordKeyword_정확히_30자_카운트_허용() {
        String exact = "a".repeat(30);
        when(searchKeywordRedisRepository.markIfAbsent(anyString(), anyString())).thenReturn(true);

        searchStatService.recordKeyword(exact, 1L, "1.2.3.4");

        verify(searchKeywordRedisRepository).increment(exact);
    }

    @Test
    void recordKeyword_dedupe_중복시_increment_skip() {
        when(searchKeywordRedisRepository.markIfAbsent(anyString(), anyString())).thenReturn(false);

        searchStatService.recordKeyword("java", 1L, "1.2.3.4");

        verify(searchKeywordRedisRepository).markIfAbsent("user:1", "java");
        verify(searchKeywordRedisRepository, never()).increment(anyString());
    }

    @Test
    void recordKeyword_requester_구분_없이_IP만_있으면_IP로() {
        when(searchKeywordRedisRepository.markIfAbsent(eq("ip:9.9.9.9"), anyString())).thenReturn(true);

        searchStatService.recordKeyword("python", null, "9.9.9.9");

        verify(searchKeywordRedisRepository).increment("python");
    }
}
