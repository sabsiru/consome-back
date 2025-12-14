package consome.domain.post;

import java.time.LocalDateTime;

public record PostSummary(
        Long postId,
        String title,
        Long authorId,
        String authorNickname,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean deleted
) {
}
