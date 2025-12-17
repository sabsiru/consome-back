package consome.application.admin;

public record PostDetailResult(
        Long postId,
        String title,
        String content,
        int authorId,
        String authorName,
        String boardName,
        String categoryName,
        int viewCount,
        int likeCount,
        int dislikeCount,
        int commentCount,
        String createdAt,
        String updatedAt
) {
}
