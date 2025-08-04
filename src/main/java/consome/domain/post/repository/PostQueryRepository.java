package consome.domain.post.repository;

import consome.domain.post.PostSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

    Page<PostSummary> findPostWithStatsByBoardId(Long boardId, Pageable pageable);
}
