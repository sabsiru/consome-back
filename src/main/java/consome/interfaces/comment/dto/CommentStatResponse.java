package consome.interfaces.comment.dto;

import consome.domain.comment.CommentStat;

public record CommentStatResponse(
        Long commentId,
        int likeCount,
        int dislikeCount
) {
    public static CommentStatResponse from(CommentStat commentStat) {
        return new CommentStatResponse(
                commentStat.getCommentId(),
                commentStat.getLikeCount(),
                commentStat.getDislikeCount()
        );
    }
}
