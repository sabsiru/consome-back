package consome.interfaces.admin.v1;

import consome.application.admin.BoardPagingResult;
import consome.application.admin.BoardSearchCommand;
import consome.application.admin.ManageFacade;
import consome.application.admin.UserPagingResult;
import consome.application.admin.result.ManageTreeResult;
import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchPagingResult;
import consome.interfaces.admin.dto.manage.BoardSearchListResponse;
import consome.interfaces.admin.dto.manage.ManageTreeResponse;
import consome.interfaces.admin.dto.manage.ManageUserListResponse;
import consome.interfaces.admin.dto.manage.UserSearchListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/manage")
public class AdminV1ManageController {

    private final ManageFacade manageFacade;

    @GetMapping("/tree")
    public ResponseEntity<ManageTreeResponse> getTree() {
        ManageTreeResult result = manageFacade.getTree();
        return ResponseEntity.ok(ManageTreeResponse.from(result));
    }

    @GetMapping("/users")
    public ResponseEntity<ManageUserListResponse> getUsers(@PageableDefault(size= 20) Pageable pageable) {
        UserPagingResult result = manageFacade.getUsers(pageable);
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
        UserSearchPagingResult result = manageFacade.searchUsers(command, pageable);
        return ResponseEntity.ok(UserSearchListResponse.from(result));
    }

    @GetMapping("/boards")
    public ResponseEntity<BoardSearchListResponse> getBoards(@PageableDefault(size = 20) Pageable pageable) {
        BoardPagingResult result = manageFacade.getBoards(pageable);
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
        BoardPagingResult result = manageFacade.searchBoards(command, pageable);
        return ResponseEntity.ok(BoardSearchListResponse.from(result));
    }
}