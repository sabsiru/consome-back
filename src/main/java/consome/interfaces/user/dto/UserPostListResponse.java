package consome.interfaces.user.dto;

import consome.application.user.UserPostResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserPostListResponse(
        List<UserPostResponse> posts,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static UserPostListResponse from(Page<UserPostResult> page) {
        List<UserPostResponse> posts = page.getContent().stream()
                .map(UserPostResponse::from)
                .toList();

        return new UserPostListResponse(
                posts,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public record UserPostResponse(
            Long postId,
            Long boardId,
            String boardName,
            String title,
            int viewCount,
            int likeCount,
            int commentCount,
            String createdAt
    ) {
        public static UserPostResponse from(UserPostResult result) {
            return new UserPostResponse(
                    result.postId(),
                    result.boardId(),
                    result.boardName(),
                    result.title(),
                    result.viewCount(),
                    result.likeCount(),
                    result.commentCount(),
                    result.createdAt().toString()
            );
        }
    }
}
