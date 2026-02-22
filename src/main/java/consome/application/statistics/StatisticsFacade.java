package consome.application.statistics;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.statistics.StatisticsService;
import consome.infrastructure.redis.VisitedBoardRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsFacade {

    private final StatisticsService statisticsService;
    private final VisitedBoardRedisRepository visitedBoardRedisRepository;
    private final BoardService boardService;

    public AdminStatisticsResult getAdminStatistics() {
        return new AdminStatisticsResult(
                statisticsService.getTotalVisitors(),
                statisticsService.getTodayVisitors(),
                statisticsService.getOnlineCount()
        );
    }

    public int getOnlineCount() {
        return statisticsService.getOnlineCount();
    }

    public List<VisitedBoardResult> getVisitedBoards(Long userId) {
        List<Long> boardIds = visitedBoardRedisRepository.getRecentVisitedBoardIds(userId);
        if (boardIds.isEmpty()) {
            return List.of();
        }

        return boardIds.stream()
                .map(boardId -> {
                    Board board = boardService.findById(boardId);
                    return new VisitedBoardResult(board.getId(), board.getName());
                })
                .toList();
    }

    public void recordBoardVisit(Long userId, Long boardId) {
        if (userId != null) {
            visitedBoardRedisRepository.recordVisit(userId, boardId);
        }
    }
}
