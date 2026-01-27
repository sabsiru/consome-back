package consome.domain.admin;

import consome.application.admin.BoardSearchCommand;
import consome.application.admin.BoardSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardQueryRepository {

    Page<BoardSearchResult> findBoards(Pageable pageable);

    Page<BoardSearchResult> search(BoardSearchCommand command, Pageable pageable);
}
