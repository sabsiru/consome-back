package consome.interfaces.comment.mapper;

import consome.domain.comment.Comment;
import consome.interfaces.comment.dto.CommentResponse;

public class CommentResponseMapper {
    public static CommentResponse toResponse(Comment comment) {
        return CommentResponse.from(comment);
    }
}
