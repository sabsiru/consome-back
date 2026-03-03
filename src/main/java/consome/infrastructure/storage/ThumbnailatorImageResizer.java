package consome.infrastructure.storage;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Component
public class ThumbnailatorImageResizer implements ImageResizer {

    @Override
    public void resize(InputStream input, Path outputPath, ImageSize size) {
        try {
            BufferedImage original = ImageIO.read(input);
            if (original == null) {
                throw new RuntimeException("이미지를 읽을 수 없습니다");
            }

            int maxDim = size.getMaxDimension();
            int origWidth = original.getWidth();
            int origHeight = original.getHeight();

            // 장축 기준 비율 유지 리사이즈
            int newWidth, newHeight;
            if (origWidth >= origHeight) {
                newWidth = Math.min(origWidth, maxDim);
                newHeight = (int) ((double) origHeight / origWidth * newWidth);
            } else {
                newHeight = Math.min(origHeight, maxDim);
                newWidth = (int) ((double) origWidth / origHeight * newHeight);
            }

            // JPEG로 저장 (WebP 쓰기는 네이티브 라이브러리 필요)
            Thumbnails.of(original)
                    .size(newWidth, newHeight)
                    .outputQuality(size.getQuality())
                    .outputFormat("jpg")
                    .toFile(outputPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("이미지 리사이징 실패", e);
        }
    }
}
