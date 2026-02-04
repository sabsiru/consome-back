package consome.application.navigation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record PopularBoardResult(
        Long boardId,
        String boardName,
        Double score,
        List<PostPreview> posts
) implements Serializable {

    public record PostPreview(
            Long postId,
            String title,
            String nickname,
            int viewCount,
            int likeCount,
            int commentCount,
            LocalDateTime createdAt
    ) implements Serializable {
    }
}
