package consome.domain.post;

import consome.domain.user.Role;

import java.time.LocalDateTime;

public record PostSummary(
        Long postId,
        String title,
        Long categoryId,
        String categoryName,
        Long authorId,
        String authorNickname,
        int authorLevel,
        Role authorRole,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean deleted
) {
}
