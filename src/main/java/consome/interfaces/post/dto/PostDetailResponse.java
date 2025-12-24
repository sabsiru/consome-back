package consome.interfaces.post.dto;

import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        Long authorId,
        Long boardId,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount
) {
    /** Post + PostStat 동시 제공 시 사용 */
    public static PostDetailResponse of(Post post, PostStat stat) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                post.getBoardId(),
                post.getCategoryId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                stat != null ? stat.getViewCount()    : 0,
                stat != null ? stat.getLikeCount()    : 0,
                stat != null ? stat.getDislikeCount() : 0,
                stat != null ? stat.getCommentCount() : 0
        );
    }

    /** 아직 PostStat이 생성되지 않았거나 별도 조회하지 않은 경우 사용(카운트 0 처리) */
    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                post.getBoardId(),
                post.getCategoryId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                0, 0, 0, 0
        );
    }
}