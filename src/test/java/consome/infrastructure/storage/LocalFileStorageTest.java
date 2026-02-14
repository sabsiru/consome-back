package consome.infrastructure.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class LocalFileStorageTest {

    @Autowired
    private LocalFileStorage localFileStorage;

    @TempDir
    Path tempDir;

    @Test
    void store_파일저장_URL반환() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test data".getBytes()
        );

        // when
        String url = localFileStorage.store(file, "posts");

        // then
        assertThat(url).startsWith("/uploads/posts/");
        assertThat(url).endsWith(".jpg");
    }
    
    @Test
    void storeAll_여러파일저장_URL리스트반환(){
        // given
        MockMultipartFile file1 = new MockMultipartFile(
                "image", "test1.jpg", "image/jpeg", "data1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "image", "test2.png", "image/png", "data2".getBytes()
        );

        // when
        List<String> urls = localFileStorage.storeAll(List.of(file1, file2), "posts");

        // then
        assertThat(urls).hasSize(2);
        assertThat(urls.get(0)).contains("/uploads/posts/");
        assertThat(urls.get(1)).contains("/uploads/posts/");
    }

    @Test
    void delete_파일삭제() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "data".getBytes()
        );
        String url = localFileStorage.store(file, "posts");

        // when
        localFileStorage.delete(url);

        // then
        Path filePath = Paths.get(url.substring(1)); // 앞 "/" 제거
        assertThat(Files.exists(filePath)).isFalse();
    }

    @Test
    void extractImageUrls_HTML에서_이미지URL추출() {
        // given
        String html = """
          <p>내용</p>
          <img src="/uploads/posts/2025/01/uuid1.jpg"/>
          <p>중간</p>
          <img src="/uploads/posts/2025/01/uuid2.png"/>
          """;

        // when
        List<String> urls = localFileStorage.extractImageUrls(html);

        // then
        assertThat(urls).hasSize(2);
        assertThat(urls).contains("/uploads/posts/2025/01/uuid1.jpg");
        assertThat(urls).contains("/uploads/posts/2025/01/uuid2.png");
    }

    @Test
    void findDeleteTargets_삭제할_URL추출() {
        // given
        List<String> existingUrls = List.of(
                "/uploads/posts/2025/01/a.jpg",
                "/uploads/posts/2025/01/b.jpg",
                "/uploads/posts/2025/01/c.jpg"
        );
        List<String> newUrls = List.of(
                "/uploads/posts/2025/01/a.jpg",
                "/uploads/posts/2025/01/c.jpg"
        );

        // when
        List<String> deleteTargets = localFileStorage.findDeleteTargets(existingUrls, newUrls);

        // then
        assertThat(deleteTargets).hasSize(1);
        assertThat(deleteTargets).contains("/uploads/posts/2025/01/b.jpg");
    }
}