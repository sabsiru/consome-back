package consome.infrastructure.storage;

import consome.domain.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {

    private final ImageResizer imageResizer;

    private static final Path UPLOADS_ROOT = Paths.get("uploads").toAbsolutePath().normalize();

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(
            ".mp4", ".mov", ".avi", ".webm", ".mkv"
    );
    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );
    private static final Set<String> ALLOWED_VIDEO_MIME_TYPES = Set.of(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/webm",
            "video/x-matroska"
    );

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    @Override
    public String store(MultipartFile file, String directory) {
        validateImageFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + extension;

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path uploadPath = Paths.get("uploads", directory, datePath);

        try {
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(storedName);
            file.transferTo(filePath);

            return "/uploads/" + directory + "/" + datePath + "/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    @Override
    public void delete(String url) {
        try {
            Path filePath = Paths.get(url.substring(1)).toAbsolutePath().normalize();
            if (!filePath.startsWith(UPLOADS_ROOT)) {
                throw new BusinessException("INVALID_PATH", "허용되지 않는 파일 경로입니다.");
            }
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    @Override
    public List<String> storeAll(List<MultipartFile> files, String directory) {
        return files.stream()
                .map(file -> store(file, directory))
                .toList();
    }

    @Override
    public List<String> extractImageUrls(String html) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String url = matcher.group(1);
            String path = extractUploadPath(url);
            if (path != null) {
                urls.add(path);
            }
        }
        return urls;
    }

    @Override
    public List<String> extractVideoUrls(String html) {
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile("<video[^>]+src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String url = matcher.group(1);
            String path = extractUploadPath(url);
            if (path != null) {
                urls.add(path);
            }
        }
        return urls;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "빈 파일입니다.");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("FILE_TOO_LARGE", "이미지 파일은 10MB 이하만 업로드 가능합니다.");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("INVALID_FILE_TYPE", "허용되지 않는 이미지 형식입니다: " + extension);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("INVALID_MIME_TYPE", "허용되지 않는 MIME 타입입니다: " + contentType);
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "빈 파일입니다.");
        }
        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new BusinessException("FILE_TOO_LARGE", "영상 파일은 100MB 이하만 업로드 가능합니다.");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_VIDEO_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("INVALID_FILE_TYPE", "허용되지 않는 영상 형식입니다: " + extension);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("INVALID_MIME_TYPE", "허용되지 않는 MIME 타입입니다: " + contentType);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BusinessException("INVALID_FILE_NAME", "파일 확장자가 없습니다.");
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String extractUploadPath(String url) {
        int idx = url.indexOf("/uploads/");
        if (idx >= 0) {
            return url.substring(idx);
        }
        return null;
    }

    @Override
    public List<String> findDeleteTargets(List<String> existingUrls, List<String> newUrls) {
        return existingUrls.stream()
                .filter(url -> !newUrls.contains(url))
                .toList();
    }

    @Override
    public String storeAndConvertVideo(MultipartFile file, String directory) {
        validateVideoFile(file);

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String tempName = UUID.randomUUID() + "_temp" + extension;
        String storedName = UUID.randomUUID() + ".mp4";

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path uploadPath = Paths.get("uploads", directory, datePath);

        try {
            Files.createDirectories(uploadPath);
            Path tempPath = uploadPath.resolve(tempName);
            Path outputPath = uploadPath.resolve(storedName);

            file.transferTo(tempPath);

            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", tempPath.toString(),
                "-vf", "scale='min(480,iw)':-2",  // 모바일 기준 480p, 비율 유지
                "-c:v", "libx264", "-preset", "fast", "-crf", "28",
                "-c:a", "aac", "-b:a", "96k", "-y",
                outputPath.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();

            Files.deleteIfExists(tempPath);

            if (exitCode != 0) {
                throw new RuntimeException("ffmpeg 변환 실패");
            }

            return "/uploads/" + directory + "/" + datePath + "/" + storedName;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("영상 변환 실패", e);
        }
    }

    @Override
    public List<String> getAllStoredFiles() {
        List<String> files = new ArrayList<>();
        Path uploadsPath = Paths.get("uploads");

        if (!Files.exists(uploadsPath)) {
            return files;
        }

        try (Stream<Path> walk = Files.walk(uploadsPath)) {
            walk.filter(Files::isRegularFile)
                .forEach(path -> files.add("/" + path.toString().replace("\\", "/")));
        } catch (IOException e) {
            throw new RuntimeException("파일 목록 조회 실패", e);
        }

        return files;
    }

    @Override
    public String[] storeWithResize(MultipartFile file, String directory) {
        validateImageFile(file);

        String uuid = UUID.randomUUID().toString();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path uploadPath = Paths.get("uploads", directory, datePath);

        try {
            Files.createDirectories(uploadPath);

            // DISPLAY 이미지 (본문용) - Thumbnailator가 .jpg 확장자 자동 추가
            Path displayPath = uploadPath.resolve(uuid);
            try (InputStream is = file.getInputStream()) {
                imageResizer.resize(is, displayPath, ImageSize.DISPLAY);
            }

            // THUMBNAIL 이미지 (목록용)
            Path thumbPath = uploadPath.resolve(uuid + "_thumb");
            try (InputStream is = file.getInputStream()) {
                imageResizer.resize(is, thumbPath, ImageSize.THUMBNAIL);
            }

            String displayUrl = "/uploads/" + directory + "/" + datePath + "/" + uuid + ".jpg";
            String thumbUrl = "/uploads/" + directory + "/" + datePath + "/" + uuid + "_thumb.jpg";

            return new String[]{displayUrl, thumbUrl};
        } catch (IOException e) {
            throw new RuntimeException("이미지 리사이징 저장 실패", e);
        }
    }
}
