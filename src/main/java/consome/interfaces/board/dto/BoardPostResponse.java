package consome.interfaces.board.dto;

import consome.application.post.PostRowResult;
import consome.domain.user.Role;

public record BoardPostResponse(
        Long postId,
        String title,
        Long categoryId,
        String categoryName,
        Long authorId,
        String authorNickName,
        int authorLevel,
        Role authorRole,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount,
        String createdAt,
        String updatedAt,
        Boolean deleted,
        Boolean isPinned,
        Integer pinnedOrder

) {
    public static BoardPostResponse from(PostRowResult row){
        return new BoardPostResponse(
                row.postId(),
                row.title(),
                row.categoryId(),
                row.categoryName(),
                row.authorId(),
                row.authorNickname(),
                row.authorLevel(),
                row.authorRole(),
                row.viewCount(),
                row.likeCount(),
                row.dislikeCount(),
                row.commentCount(),
                row.createdAt().toString(),
                row.updatedAt().toString(),
                row.deleted(),
                row.isPinned(),
                row.pinnedOrder()
        );
    }
}
