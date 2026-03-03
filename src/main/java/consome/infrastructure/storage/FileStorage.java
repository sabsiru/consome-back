package consome.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorage {

    String store(MultipartFile file, String directory);

    void delete(String url);

    List<String>  storeAll(List<MultipartFile> files, String directory);

    List<String> extractImageUrls(String html);

    List<String> extractVideoUrls(String html);

    List<String> findDeleteTargets(List<String> existingUrls, List<String> newUrls);

    String storeAndConvertVideo(MultipartFile file, String directory);

    List<String> getAllStoredFiles();

    /**
     * 이미지를 리사이징하여 저장
     * @return [0]: display URL, [1]: thumbnail URL
     */
    String[] storeWithResize(MultipartFile file, String directory);
}
