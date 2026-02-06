package consome.infrastructure.scheduler;

import consome.domain.admin.Board;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.admin.repository.BoardStatQueryRepository;
import consome.domain.admin.repository.BoardStatQueryRepository.BoardStatRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardStatScheduler {

    private final BoardRepository boardRepository;
    private final BoardStatQueryRepository boardStatQueryRepository;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void updateBoardStats() {
        log.info("게시판 통계 갱신 시작");

        List<BoardStatRow> stats = boardStatQueryRepository.findBoardStats();
        Map<Long, BoardStatRow> statMap = stats.stream()
                .collect(Collectors.toMap(BoardStatRow::boardId, Function.identity()));

        List<Board> boards = boardRepository.findByDeletedFalseOrderByDisplayOrder();
        for (Board board : boards) {
            BoardStatRow stat = statMap.get(board.getId());
            if (stat != null) {
                board.updateStats(stat.avgViewCount(), stat.avgLikeCount(), stat.avgCommentCount());
            }
        }

        log.info("게시판 통계 갱신 완료: {} 개", boards.size());
    }
}
