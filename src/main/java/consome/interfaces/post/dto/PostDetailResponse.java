package consome.interfaces.post.dto;

import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        Long authorId,
        String authorNickname,
        String authorRole,
        int authorLevel,
        Long boardId,
        String boardName,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount,
        boolean hasLiked,
        boolean hasDisliked,
        boolean commentEnabled
) {
    public static PostDetailResponse of(Post post, PostStat stat, String authorNickname, String authorRole, int authorLevel, String boardName, boolean hasLiked, boolean hasDisliked, boolean commentEnabled) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                authorNickname,
                authorRole,
                authorLevel,
                post.getBoardId(),
                boardName,
                post.getCategoryId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                stat != null ? stat.getViewCount()    : 0,
                stat != null ? stat.getLikeCount()    : 0,
                stat != null ? stat.getDislikeCount() : 0,
                stat != null ? stat.getCommentCount() : 0,
                hasLiked,
                hasDisliked,
                commentEnabled
        );
    }

    public static PostDetailResponse from(Post post, String authorNickname, String authorRole, int authorLevel, String boardName, boolean commentEnabled) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                authorNickname,
                authorRole,
                authorLevel,
                post.getBoardId(),
                boardName,
                post.getCategoryId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                0, 0, 0, 0,
                false,
                false,
                commentEnabled
        );
    }
}
