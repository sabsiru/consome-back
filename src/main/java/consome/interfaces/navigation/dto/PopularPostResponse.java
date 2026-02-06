package consome.interfaces.navigation.dto;

import consome.application.navigation.PopularPostResult;

import java.time.LocalDateTime;
import java.util.List;

public record PopularPostResponse(
        Long postId,
        Long boardId,
        String boardName,
        String title,
        String nickname,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
    public static PopularPostResponse from(PopularPostResult result) {
        return new PopularPostResponse(
                result.postId(),
                result.boardId(),
                result.boardName(),
                result.title(),
                result.nickname(),
                result.viewCount(),
                result.likeCount(),
                result.commentCount(),
                result.createdAt()
        );
    }

    public static List<PopularPostResponse> fromList(List<PopularPostResult> results) {
        return results.stream()
                .map(PopularPostResponse::from)
                .toList();
    }
}
