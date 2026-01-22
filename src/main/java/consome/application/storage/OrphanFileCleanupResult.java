package consome.application.storage;

public record OrphanFileCleanupResult(
    int totalFiles,
    int referencedFiles,
    int deletedFiles
) {}
