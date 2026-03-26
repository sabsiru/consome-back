package consome.application.navigation;

import java.io.Serializable;
import java.util.List;

/**
 * Redis 캐싱 가능한 Page 래퍼.
 * PageImpl은 Jackson 역직렬화가 불가하므로 이 DTO를 대신 캐싱한다.
 */
public record CachedPage<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) implements Serializable {

    public static <T> CachedPage<T> from(org.springframework.data.domain.Page<T> page) {
        return new CachedPage<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
