package consome.application.post;

import java.time.LocalDateTime;

public record EditResult(
        Long postId,
        LocalDateTime updatedAt
) {
    public static EditResult of(Long postId, LocalDateTime updatedAt) {
        return new EditResult(postId, updatedAt);
    }
}
