package consome.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorage {

    String store(MultipartFile file, String directory);

    void delete(String url);

    List<String>  storeAll(List<MultipartFile> files, String directory);

    List<String> extractImageUrls(String html);

    List<String> findDeleteTargets(List<String> existingUrls, List<String> newUrls);
}
