package consome.application.navigation;

import consome.domain.admin.Board;
import consome.domain.admin.BoardService;
import consome.domain.admin.Section;
import consome.domain.admin.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NavigationFacade {
    private final SectionService sectionService;
    private final BoardService boardService;

    public List<Section> getSections() {
        return sectionService.findAllOrdered();
    }

    public List<Board> getBoards(Long sectionId) {
        return boardService.findAllBySectionId(sectionId);
    }
}
