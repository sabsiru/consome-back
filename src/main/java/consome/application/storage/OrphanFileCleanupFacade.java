package consome.application.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrphanFileCleanupFacade {

    private final OrphanFileCleanupService orphanFileCleanupService;

    public OrphanFileCleanupResult cleanupOrphanFiles() {
        return orphanFileCleanupService.cleanup();
    }
}
