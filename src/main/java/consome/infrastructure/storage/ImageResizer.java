package consome.infrastructure.storage;

import java.io.InputStream;
import java.nio.file.Path;

public interface ImageResizer {

    /**
     * 이미지를 리사이즈하여 WebP로 저장
     * @param input 원본 이미지 스트림
     * @param outputPath 저장 경로
     * @param size 리사이즈 사이즈 (DISPLAY 또는 THUMBNAIL)
     */
    void resize(InputStream input, Path outputPath, ImageSize size);
}
