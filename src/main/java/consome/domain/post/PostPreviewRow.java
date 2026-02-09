package consome.domain.post;

import java.time.LocalDateTime;

public record PostPreviewRow(
        Long postId,
        Long boardId,
        String title,
        String nickname,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
}
