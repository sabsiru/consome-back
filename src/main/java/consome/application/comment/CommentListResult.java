package consome.application.comment;

import java.time.LocalDateTime;

public record CommentListResult(
        Long commentId,
        Long postId,
        Long userId,
        String userNickname,
        Long parentId,
        String parentUserNickname,
        String content,
        int depth,
        int likeCount,
        int dislikeCount,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean hasLiked,
        boolean hasDisliked
) {
}
