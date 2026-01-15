package consome.interfaces.comment.dto;

import java.time.LocalDateTime;

public record CommentListResponse(
        Long commentId,
        Long postId,
        Long userId,
        String userNickname,
        Long parentId,
        String parentUserNickname,
        String content,
        int depth,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
