package consome.interfaces.navigation.v1;

import consome.application.navigation.NavigationFacade;
import consome.application.navigation.SectionResult;
import consome.interfaces.navigation.dto.SectionHeaderResponse;
import consome.interfaces.navigation.dto.BoardItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/navigation")
public class NavigationController {
    private final NavigationFacade navigationFacade;

    @GetMapping("/sections")
    public List<SectionHeaderResponse> getSections() {
        return SectionHeaderResponse.fromSections(navigationFacade.getSections());
    }

    @GetMapping("/sections/{sectionId}/boards")
    public List<BoardItemResponse> getBoards(@PathVariable Long sectionId) {
        return BoardItemResponse.fromBoards(navigationFacade.getBoards(sectionId));
    }

    @GetMapping("/header")
    public ResponseEntity<List<SectionResult>> getHeader() {
        return ResponseEntity.ok(navigationFacade.getHeaderSections());
    }
}
