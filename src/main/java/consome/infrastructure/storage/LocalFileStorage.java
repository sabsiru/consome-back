package consome.infrastructure.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LocalFileStorage implements FileStorage {

    @Override
    public String store(MultipartFile file, String directory) {
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
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
            Path filePath = Paths.get(url.substring(1));
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
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
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
                "-c:v", "libx264", "-preset", "fast",
                "-c:a", "aac", "-y",
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
}
