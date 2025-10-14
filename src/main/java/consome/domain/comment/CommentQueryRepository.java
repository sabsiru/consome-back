package consome.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentQueryRepository{
    Page<Comment> findCommentsByPostId(Long postId, Pageable pageable);
}
