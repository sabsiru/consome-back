package consome.interfaces.comment.mapper;

import consome.domain.comment.Comment;
import consome.interfaces.comment.dto.CommentPageResponse;
import consome.interfaces.comment.dto.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class CommentResponseMapper {

    public static CommentPageResponse toPageResponse(Page<Comment> page) {
        List<CommentResponse> comments = page.getContent().stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getPostId(),
                        comment.getUserId(),
                        comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent(),
                        comment.getCreatedAt()
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
