package consome.application.comment;

import java.time.LocalDateTime;

public record CommentResult(
        Long commentId,
        Long postId,
        Long userId,
        String userNickname,
        String content,
        int depth,
        Boolean isDeleted,
        LocalDateTime createdAt
) {
}
