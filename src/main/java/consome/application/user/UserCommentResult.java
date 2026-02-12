package consome.application.user;

import java.time.LocalDateTime;

public record UserCommentResult(
        Long commentId,
        Long postId,
        String postTitle,
        Long boardId,
        String boardName,
        String content,
        int likeCount,
        LocalDateTime createdAt
) {
}
