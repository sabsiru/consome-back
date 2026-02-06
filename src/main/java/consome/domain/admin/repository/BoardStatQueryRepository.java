package consome.domain.admin.repository;

import java.util.List;

public interface BoardStatQueryRepository {

    List<BoardStatRow> findBoardStats();

    record BoardStatRow(
            Long boardId,
            double avgViewCount,
            double avgLikeCount,
            double avgCommentCount
    ) {}
}
