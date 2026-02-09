package consome.domain.post;

import java.time.LocalDateTime;

public record PostSummary(
        Long postId,
        String title,
        Long categoryId,
        String categoryName,
        Long authorId,
        String authorNickname,
        int authorLevel,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean deleted
) {
}
