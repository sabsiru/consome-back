package consome.application.admin;

import java.time.LocalDateTime;

public record BoardPostRowResult(
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
