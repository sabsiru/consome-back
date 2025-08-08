package consome.domain.post;

import java.time.LocalDateTime;

public record PostSummary(
        Long postId,
        String title,
        Long authorId,
        LocalDateTime createdAt,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount
) {
}
