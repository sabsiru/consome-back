package consome.domain.post.repository;

import consome.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    @Modifying
    @Query("UPDATE PostImage p SET p.deleted = true WHERE p.postId = :postId")
    void softDeleteByPostId(Long postId);
}
