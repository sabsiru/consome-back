package consome.interfaces.comment.mapper;

import consome.application.comment.CommentResult;
import consome.domain.comment.Comment;
import consome.interfaces.comment.dto.CommentPageResponse;
import consome.interfaces.comment.dto.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class CommentResponseMapper {

    public static CommentPageResponse toPageResponse(Page<CommentResult> page) {
        List<CommentResponse> comments = page.getContent().stream()
                .map(comment -> new CommentResponse(
                        comment.commentId(),
                        comment.postId(),
                        comment.userId(),
                        comment.userNickname(),
                        comment.isDeleted() ? "삭제된 댓글입니다." : comment.content(),
                        comment.depth(),
                        comment.createdAt()
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
