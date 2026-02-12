package consome.interfaces.user.dto;

import consome.application.user.UserCommentResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserCommentListResponse(
        List<UserCommentResponse> comments,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static UserCommentListResponse from(Page<UserCommentResult> page) {
        List<UserCommentResponse> comments = page.getContent().stream()
                .map(UserCommentResponse::from)
                .toList();

        return new UserCommentListResponse(
                comments,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public record UserCommentResponse(
            Long commentId,
            Long postId,
            String postTitle,
            Long boardId,
            String boardName,
            String content,
            int likeCount,
            String createdAt
    ) {
        public static UserCommentResponse from(UserCommentResult result) {
            return new UserCommentResponse(
                    result.commentId(),
                    result.postId(),
                    result.postTitle(),
                    result.boardId(),
                    result.boardName(),
                    result.content(),
                    result.likeCount(),
                    result.createdAt().toString()
            );
        }
    }
}
