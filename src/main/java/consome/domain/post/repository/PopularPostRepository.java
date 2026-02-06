package consome.domain.post.repository;

import consome.domain.post.entity.PopularPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopularPostRepository extends JpaRepository<PopularPost, Long> {

    List<PopularPost> findAllByOrderByCreatedAtDesc();

    boolean existsByPostId(Long postId);
}
