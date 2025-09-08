package consome.interfaces.post.dto;

import consome.application.post.EditResult;

import java.time.LocalDateTime;

public record EditResponse(
        Long postId,
        LocalDateTime updatedAt
) {
    public static EditResponse from(EditResult result) {
        return new EditResponse(
                result.postId(),
                result.updatedAt()
        );
    }
}
