package consome.interfaces.comment.dto;

import java.time.LocalDateTime;

public record CommentListResponse(
        Long commentId,
        Long postId,
        Long userId,
        String userNickname,
        int userLevel,
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
