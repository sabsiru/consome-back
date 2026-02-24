package consome.interfaces.navigation.v1;

import consome.application.navigation.BoardResult;
import consome.application.navigation.NavigationFacade;
import consome.application.navigation.PopularBoardCriteria;
import consome.application.navigation.PopularPostCriteria;
import consome.domain.post.PopularityType;
import consome.interfaces.navigation.dto.BoardItemResponse;
import consome.interfaces.navigation.dto.FeaturedBoardsResponse;
import consome.interfaces.navigation.dto.PopularBoardResponse;
import consome.interfaces.navigation.dto.PopularPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/main-boards")
    public ResponseEntity<List<BoardResult>> getMainBoards() {
        return ResponseEntity.ok(navigationFacade.getMainBoards());
    }

    @GetMapping("/popular-boards")
    public ResponseEntity<List<PopularBoardResponse>> getPopularBoards(
            @RequestParam(defaultValue = "6") int boardLimit,
            @RequestParam(defaultValue = "5") int previewLimit,
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "COMPOSITE") PopularityType sortBy
    ) {
        PopularBoardCriteria criteria = new PopularBoardCriteria(boardLimit, previewLimit, days, sortBy);
        List<PopularBoardResponse> response = PopularBoardResponse.fromList(
                navigationFacade.getPopularBoards(criteria)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular-posts")
    public ResponseEntity<List<PopularPostResponse>> getPopularPosts(
            @RequestParam(defaultValue = "20") int limit
    ) {
        PopularPostCriteria criteria = new PopularPostCriteria(limit);
        List<PopularPostResponse> response = PopularPostResponse.fromList(
                navigationFacade.getPopularPosts(criteria)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured-boards")
    public ResponseEntity<FeaturedBoardsResponse> getFeaturedBoards() {
        return ResponseEntity.ok(FeaturedBoardsResponse.from(navigationFacade.getFeaturedBoards()));
    }
}
