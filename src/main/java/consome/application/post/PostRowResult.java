package consome.application.post;

import consome.domain.admin.Board;

import java.time.LocalDateTime;

public record PostRowResult(
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
