package consome.domain.post.repository;

import consome.domain.post.entity.TempPostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempPostImageRepository extends JpaRepository<TempPostImage, Long> {
    Optional<TempPostImage> findByUrl(String url);
}
