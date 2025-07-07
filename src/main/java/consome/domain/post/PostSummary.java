package consome.domain.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSummary {
    private final Long postId;
    private final String title;
    private final Long authorId;
    private final LocalDateTime createdAt;
    private final int viewCount;
    private final int likeCount;
    private final int dislikeCount;
    private final int commentCount;
}
