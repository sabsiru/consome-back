package consome.interfaces.post.dto;

import consome.domain.post.entity.PostStat;

public record PostStatResponse(
        Long postId,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount
) {
    public static PostStatResponse from(PostStat postStat) {
        return new PostStatResponse(postStat.getPostId(),
                postStat.getViewCount(),
                postStat.getLikeCount(),
                postStat.getDislikeCount(),
                postStat.getCommentCount()
        );
    }
}
