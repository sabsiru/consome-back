package consome.application.storage;

import consome.domain.post.entity.Post;
import consome.domain.post.repository.PostRepository;
import consome.infrastructure.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrphanFileCleanupService {

    private final PostRepository postRepository;
    private final FileStorage fileStorage;

    public OrphanFileCleanupResult cleanup() {
        List<Post> allPosts = postRepository.findAll();

        Set<String> referencedUrls = new HashSet<>();
        for (Post post : allPosts) {
            String content = post.getContent();
            if (content != null) {
                referencedUrls.addAll(fileStorage.extractImageUrls(content));
                referencedUrls.addAll(fileStorage.extractVideoUrls(content));
            }
        }

        List<String> storedFiles = fileStorage.getAllStoredFiles();

        int deletedCount = 0;
        for (String filePath : storedFiles) {
            if (!referencedUrls.contains(filePath)) {
                try {
                    fileStorage.delete(filePath);
                    deletedCount++;
                    log.info("고아 파일 삭제: {}", filePath);
                } catch (Exception e) {
                    log.error("파일 삭제 실패: {}", filePath, e);
                }
            }
        }

        return new OrphanFileCleanupResult(storedFiles.size(), referencedUrls.size(), deletedCount);
    }
}
