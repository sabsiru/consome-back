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
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
