package consome.application.navigation;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PopularPostResult(
        Long postId,
        Long boardId,
        String boardName,
        String title,
        String nickname,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) implements Serializable {}
