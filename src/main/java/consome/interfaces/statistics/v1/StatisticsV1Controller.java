package consome.interfaces.statistics.v1;

import consome.application.statistics.StatisticsFacade;
import consome.application.statistics.VisitedBoardResult;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.statistics.dto.AdminStatisticsResponse;
import consome.interfaces.statistics.dto.OnlineCountResponse;
import consome.interfaces.statistics.dto.VisitedBoardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StatisticsV1Controller {

    private final StatisticsFacade statisticsFacade;

    /**
     * 관리자 전체 통계 조회
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<AdminStatisticsResponse> getAdminStatistics() {
        var result = statisticsFacade.getAdminStatistics();
        return ResponseEntity.ok(AdminStatisticsResponse.from(result));
    }

    /**
     * 현재 접속자 수 (공개)
     */
    @GetMapping("/statistics/online-count")
    public ResponseEntity<OnlineCountResponse> getOnlineCount() {
        int count = statisticsFacade.getOnlineCount();
        return ResponseEntity.ok(new OnlineCountResponse(count));
    }

    /**
     * 개인 최근 방문 게시판
     */
    @GetMapping("/users/me/visited-boards")
    public ResponseEntity<VisitedBoardsResponse> getVisitedBoards(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(new VisitedBoardsResponse(List.of()));
        }
        List<VisitedBoardResult> results = statisticsFacade.getVisitedBoards(userDetails.getUserId());
        return ResponseEntity.ok(VisitedBoardsResponse.from(results));
    }
}
