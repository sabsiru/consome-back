package consome.interfaces.comment.mapper;

import consome.application.comment.CommentListResult;
import consome.application.comment.CommentPageResult;
import consome.interfaces.comment.dto.CommentListResponse;
import consome.interfaces.comment.dto.CommentPageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class CommentResponseMapper {

    public static CommentPageResponse toPageResponse(CommentPageResult result) {
        Page<CommentListResult> page = result.comments();

        List<CommentListResponse> popularComments = result.popularComments().stream()
                .map(CommentResponseMapper::toResponse)
                .toList();

        List<CommentListResponse> comments = page.getContent().stream()
                .map(CommentResponseMapper::toResponse)
                .toList();

        return new CommentPageResponse(
                popularComments,
                comments,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    private static CommentListResponse toResponse(CommentListResult comment) {
        return new CommentListResponse(
                comment.commentId(),
                comment.postId(),
                comment.userId(),
                comment.userNickname(),
                comment.userLevel(),
                comment.parentId(),
                comment.parentUserNickname(),
                comment.isDeleted() ? "삭제된 댓글입니다." : comment.content(),
                comment.depth(),
                comment.likeCount(),
                comment.dislikeCount(),
                comment.isDeleted(),
                comment.createdAt(),
                comment.updatedAt(),
                comment.hasLiked(),
                comment.hasDisliked()
        );
    }
}
