package consome.domain.post.repository;

import consome.domain.post.BoardPopularityRow;
import consome.domain.post.PopularPostRow;
import consome.domain.post.PopularityType;
import consome.domain.post.PostPreviewRow;
import consome.domain.post.PostSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {

    Page<PostSummary> findPostWithStatsByBoardId(Long boardId, Pageable pageable, Long categoryId);

    List<BoardPopularityRow> findPopularBoards(LocalDateTime since, PopularityType sortBy, int limit);

    List<PostPreviewRow> findLatestPostsByBoardIds(List<Long> boardIds, int previewLimit);

    List<PopularPostRow> findPopularPosts(LocalDateTime since, int minViews);

    Page<PostSummary> searchPosts(Long boardId, String keyword, String searchType, Pageable pageable);
}
