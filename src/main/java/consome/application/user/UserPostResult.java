package consome.application.user;

import java.time.LocalDateTime;

public record UserPostResult(
        Long postId,
        Long boardId,
        String boardName,
        String title,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
}
