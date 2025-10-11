package consome.domain.comment;

import java.util.List;

public interface CommentQueryRepository{
    List<Comment> findCommentsByPostId(Long postId, Long cursorId, int size, String sort);
}
