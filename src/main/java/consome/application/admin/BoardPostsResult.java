package consome.application.admin;

import java.time.LocalDateTime;

public record BoardPostsResult(
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
