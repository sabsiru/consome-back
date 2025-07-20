package consome.interfaces.navigation.v1;

import consome.application.navigation.NavigationFacade;
import consome.domain.board.Board;
import consome.domain.board.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class NavigationController {
    private final NavigationFacade mainFacade;

    @GetMapping("/sections")
    public List<Section> getSections() {
        return mainFacade.getSections();
    }

    @GetMapping("/sections/{sectionId}/boards")
    public List<Board> getBoards(@PathVariable Long sectionId) {
        return mainFacade.getBoards(sectionId);
    }
}
