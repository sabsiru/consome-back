package consome.interfaces.admin.v1;

import consome.application.admin.BoardPagingResult;
import consome.application.admin.BoardSearchCommand;
import consome.application.admin.AdminDashboardFacade;
import consome.application.admin.UserPagingResult;
import consome.application.admin.result.ManageTreeResult;
import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchPagingResult;
import consome.interfaces.admin.dto.ManagerResponse;
import consome.interfaces.admin.dto.manage.BoardSearchListResponse;
import consome.interfaces.admin.dto.manage.ManageTreeResponse;
import consome.interfaces.admin.dto.manage.ManageUserListResponse;
import consome.interfaces.admin.dto.manage.UserSearchListResponse;
import consome.interfaces.admin.dto.SuspendUserRequest;
import consome.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/manage")
public class AdminV1DashboardController {

    private final AdminDashboardFacade adminDashboardFacade;

    @GetMapping("/tree")
    public ResponseEntity<ManageTreeResponse> getTree() {
        ManageTreeResult result = adminDashboardFacade.getTree();
        return ResponseEntity.ok(ManageTreeResponse.from(result));
    }

    @GetMapping("/users")
    public ResponseEntity<ManageUserListResponse> getUsers(@PageableDefault(size= 20) Pageable pageable) {
        UserPagingResult result = adminDashboardFacade.getUsers(pageable);
        return ResponseEntity.ok(ManageUserListResponse.from(result));
    }

    @GetMapping("/users/search")
    public ResponseEntity<UserSearchListResponse> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String nickname,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UserSearchCommand command = new UserSearchCommand(keyword, id, loginId, nickname);
        UserSearchPagingResult result = adminDashboardFacade.searchUsers(command, pageable);
        return ResponseEntity.ok(UserSearchListResponse.from(result));
    }

    @GetMapping("/boards")
    public ResponseEntity<BoardSearchListResponse> getBoards(@PageableDefault(size = 20) Pageable pageable) {
        BoardPagingResult result = adminDashboardFacade.getBoards(pageable);
        return ResponseEntity.ok(BoardSearchListResponse.from(result));
    }

    @GetMapping("/boards/search")
    public ResponseEntity<BoardSearchListResponse> searchBoards(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        BoardSearchCommand command = new BoardSearchCommand(keyword, id, name);
        BoardPagingResult result = adminDashboardFacade.searchBoards(command, pageable);
        return ResponseEntity.ok(BoardSearchListResponse.from(result));
    }

    @PostMapping("/users/{userId}/role")
    public ResponseEntity<ManagerResponse> assignManager(
            @PathVariable Long userId,
            @RequestParam Long boardId) {
        adminDashboardFacade.assignManager(boardId, userId);
        var result = adminDashboardFacade.getManagersByBoard(boardId).stream()
                .filter(m -> m.userId().equals(userId))
                .findFirst()
                .orElseThrow();
        return ResponseEntity.ok(ManagerResponse.from(result));
    }

    @DeleteMapping("/users/{userId}/role")
    public ResponseEntity<Void> removeManager(
            @PathVariable Long userId,
            @RequestParam Long boardId) {
        adminDashboardFacade.removeManager(boardId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(
            @PathVariable Long userId,
            @RequestParam Long adminId,
            @RequestBody SuspendUserRequest request) {
        adminDashboardFacade.suspendUser(userId, request.type(), request.reason(), adminId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}/suspend")
    public ResponseEntity<Void> unsuspendUser(@PathVariable Long userId) {
        adminDashboardFacade.unsuspendUser(userId);
        return ResponseEntity.ok().build();
    }
}