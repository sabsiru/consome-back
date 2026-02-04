package consome.interfaces.navigation.dto;

import consome.application.navigation.PopularBoardResult;

import java.time.LocalDateTime;
import java.util.List;

public record PopularBoardResponse(
        Long boardId,
        String boardName,
        Double score,
        List<PostPreviewResponse> posts
) {
    public record PostPreviewResponse(
            Long postId,
            String title,
            String nickname,
            int viewCount,
            int likeCount,
            int commentCount,
            LocalDateTime createdAt
    ) {
        public static PostPreviewResponse from(PopularBoardResult.PostPreview preview) {
            return new PostPreviewResponse(
                    preview.postId(),
                    preview.title(),
                    preview.nickname(),
                    preview.viewCount(),
                    preview.likeCount(),
                    preview.commentCount(),
                    preview.createdAt()
            );
        }
    }

    public static PopularBoardResponse from(PopularBoardResult result) {
        return new PopularBoardResponse(
                result.boardId(),
                result.boardName(),
                result.score(),
                result.posts().stream()
                        .map(PostPreviewResponse::from)
                        .toList()
        );
    }

    public static List<PopularBoardResponse> fromList(List<PopularBoardResult> results) {
        return results.stream()
                .map(PopularBoardResponse::from)
                .toList();
    }
}
