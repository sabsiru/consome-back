package consome.domain.admin.repository;

import consome.application.admin.BoardSearchCommand;
import consome.application.admin.BoardSearchResult;
import consome.application.board.UserBoardSearchResult;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardQueryRepository {

    Page<BoardSearchResult> findBoards(Pageable pageable);

    Page<BoardSearchResult> search(BoardSearchCommand command, Pageable pageable);

    List<UserBoardSearchResult> searchByKeyword(String keyword, int limit);
}
