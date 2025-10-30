package consome.interfaces.admin.v1;

import consome.application.admin.ManageFacade;
import consome.application.admin.result.ManageTreeResult;
import consome.interfaces.admin.dto.manage.ManageTreeResponse;
import lombok.RequiredArgsConstructor;
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
}