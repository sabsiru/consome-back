package consome.interfaces.navigation.v1;

import consome.application.navigation.BoardResult;
import consome.application.navigation.NavigationFacade;
import consome.interfaces.navigation.dto.BoardItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/navigation")
public class NavigationV1Controller {
    private final NavigationFacade navigationFacade;

    @GetMapping("/boards")
    public List<BoardItemResponse> getBoards() {
        return BoardItemResponse.fromBoards(navigationFacade.getBoards());
    }

    @GetMapping("/header")
    public ResponseEntity<List<BoardResult>> getHeader() {
        return ResponseEntity.ok(navigationFacade.getHeaderBoards());
    }
}
