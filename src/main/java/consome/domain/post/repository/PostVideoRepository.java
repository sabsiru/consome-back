package consome.domain.post.repository;

import consome.domain.post.entity.PostVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostVideoRepository extends JpaRepository<PostVideo, Long> {
    List<PostVideo> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    @Modifying
    @Query("UPDATE PostVideo p SET p.deleted = true WHERE p.postId = :postId")
    void softDeleteByPostId(Long postId);
}
