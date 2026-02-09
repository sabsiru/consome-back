package consome.domain.post;

import java.time.LocalDateTime;

public record PostPreviewRow(
        Long postId,
        Long boardId,
        String title,
        String nickname,
        int authorLevel,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
}
