package consome.application.navigation;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.post.BoardPopularityRow;
import consome.domain.post.PostPreviewRow;
import consome.domain.post.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavigationFacade {
    private final BoardService boardService;
    private final PostQueryRepository postQueryRepository;

    public List<BoardResult> getHeaderBoards() {
        List<Board> boards = boardService.findAllOrdered();

        return boards.stream()
                .map(board -> new BoardResult(
                        board.getId(),
                        board.getName(),
                        board.getDisplayOrder()
                ))
                .toList();
    }

    public List<Board> getBoards() {
        return boardService.findAllOrdered();
    }

    @Cacheable(
            value = "popular-boards",
            key = "#criteria.sortBy + ':' + #criteria.days + ':' + #criteria.boardLimit + ':' + #criteria.previewLimit"
    )
    public List<PopularBoardResult> getPopularBoards(PopularBoardCriteria criteria) {
        LocalDateTime since = LocalDateTime.now().minusDays(criteria.days());

        // 1. 인기 게시판 조회
        List<BoardPopularityRow> popularBoards = postQueryRepository.findPopularBoards(
                since, criteria.sortBy(), criteria.boardLimit()
        );

        if (popularBoards.isEmpty()) {
            return List.of();
        }

        // 2. 게시판별 최신 게시글 조회
        List<Long> boardIds = popularBoards.stream()
                .map(BoardPopularityRow::boardId)
                .toList();

        List<PostPreviewRow> previews = postQueryRepository.findLatestPostsByBoardIds(
                boardIds, criteria.previewLimit()
        );

        Map<Long, List<PostPreviewRow>> previewMap = previews.stream()
                .collect(Collectors.groupingBy(PostPreviewRow::boardId));

        // 3. 결과 조합
        return popularBoards.stream()
                .map(board -> new PopularBoardResult(
                        board.boardId(),
                        board.boardName(),
                        board.score(),
                        previewMap.getOrDefault(board.boardId(), List.of()).stream()
                                .map(p -> new PopularBoardResult.PostPreview(
                                        p.postId(),
                                        p.title(),
                                        p.nickname(),
                                        p.viewCount(),
                                        p.likeCount(),
                                        p.commentCount(),
                                        p.createdAt()
                                ))
                                .toList()
                ))
                .toList();
    }
}
