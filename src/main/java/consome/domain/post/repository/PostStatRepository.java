package consome.domain.post.repository;

import consome.domain.post.entity.PostStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStatRepository extends JpaRepository<PostStat, Long> {
}
