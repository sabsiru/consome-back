package consome.interfaces.comment.mapper;

import consome.application.comment.CommentListResult;
import consome.interfaces.comment.dto.CommentListResponse;
import consome.interfaces.comment.dto.CommentPageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class CommentResponseMapper {

    public static CommentPageResponse toPageResponse(Page<CommentListResult> page) {
        List<CommentListResponse> comments = page.getContent().stream()
                .map(comment -> new CommentListResponse(
                        comment.commentId(),
                        comment.postId(),
                        comment.userId(),
                        comment.userNickname(),
                        comment.parentId(),
                        comment.parentUserNickname(),
                        comment.isDeleted() ? "삭제된 댓글입니다." : comment.content(),
                        comment.depth(),
                        comment.isDeleted(),
                        comment.createdAt(),
                        comment.updatedAt()
                ))
                .toList();

        return new CommentPageResponse(
                comments,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }
}
