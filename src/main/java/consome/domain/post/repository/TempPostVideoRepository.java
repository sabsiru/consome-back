package consome.domain.post.repository;

import consome.domain.post.entity.TempPostVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempPostVideoRepository extends JpaRepository<TempPostVideo, Long> {
    Optional<TempPostVideo> findByUrl(String url);
}
