package consome.interfaces.admin.v1;

import consome.application.admin.ManageFacade;
import consome.application.admin.UserPagingResult;
import consome.application.admin.result.ManageTreeResult;
import consome.interfaces.admin.dto.manage.ManageTreeResponse;
import consome.interfaces.admin.dto.manage.ManageUserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        System.out.println("[AdminV1ManageController] /api/v1/admin/manage/users page="
                + pageable.getPageNumber() + ", size=" + pageable.getPageSize());
        UserPagingResult result = manageFacade.getUsers(pageable);
        return ResponseEntity.ok(ManageUserListResponse.from(result));
    }
}