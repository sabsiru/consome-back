package consome.interfaces.admin.v1;

import consome.application.storage.OrphanFileCleanupResult;
import consome.application.storage.OrphanFileCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/storage")
public class AdminV1StorageController {

    private final OrphanFileCleanupService orphanFileCleanupService;

    @DeleteMapping("/orphan-files")
    public ResponseEntity<OrphanFileCleanupResult> cleanupOrphanFiles() {
        OrphanFileCleanupResult result = orphanFileCleanupService.cleanup();
        return ResponseEntity.ok(result);
    }
}
