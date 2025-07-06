package consome.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

    Page<PostSummary> findPostWithStatsByBoardId(Long boardId, Pageable pageable);
}
