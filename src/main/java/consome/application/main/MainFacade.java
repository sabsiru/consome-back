package consome.application.main;

import consome.domain.board.Board;
import consome.domain.board.BoardService;
import consome.domain.board.Section;
import consome.domain.board.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainFacade {
    private final SectionService sectionService;
    private final BoardService boardService;

    public List<Section> getSections() {
        return sectionService.getSections();
    }

    public List<Board> getBoards(Long sectionId) {
        return boardService.getBoards(sectionId);
    }
}
