package consome.interfaces.admin.v1;

import consome.application.storage.OrphanFileCleanupFacade;
import consome.application.storage.OrphanFileCleanupResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/storage")
public class AdminV1StorageController {

    private final OrphanFileCleanupFacade orphanFileCleanupFacade;

    @DeleteMapping("/orphan-files")
    public ResponseEntity<OrphanFileCleanupResult> cleanupOrphanFiles() {
        OrphanFileCleanupResult result = orphanFileCleanupFacade.cleanupOrphanFiles();
        return ResponseEntity.ok(result);
    }
}
