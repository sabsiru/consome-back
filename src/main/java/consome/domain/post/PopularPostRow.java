package consome.domain.post;

import java.time.LocalDateTime;

public record PopularPostRow(
        Long postId,
        Long boardId,
        String boardName,
        String title,
        String nickname,
        int authorLevel,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {}
