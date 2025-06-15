package consome.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostStatRepository statRepository;
    private final PostLikeRepository likeRepository;

    public void write(Post post) {
        PostStat stat = PostStat.init(post);
        postRepository.save(post);
        statRepository.save(stat);
    }

    public void like(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());


        if (likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.LIKE).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }
        PostLike postLike = PostLike.like(post.getId(), userId);
        postStat.increaseLikeCount();

        likeRepository.save(postLike);
        statRepository.save(postStat);
    }

    public void dislike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());

        if (likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.DISLIKE).isPresent()) {
            throw new IllegalStateException("이미 싫어요를 눌렀습니다.");
        }
        PostLike postLike = PostLike.disLike(post.getId(), userId);
        postStat.increaseDislikeCount();

        likeRepository.save(postLike);
        statRepository.save(postStat);
    }

    public void cancelLike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());
        Optional<PostLike> existingLike = likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.LIKE);
        if (likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.LIKE).isEmpty()) {
            throw new IllegalStateException("좋아요를 누르지 않았습니다.");
        }
        PostLike postLike = existingLike.get();
        postLike.cancel();
        postStat.decreaseLikeCount();

        likeRepository.save(postLike);
        statRepository.save(postStat);
    }

    public void cancelDislike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());
        Optional<PostLike> existingDislike = likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.DISLIKE);
        if (existingDislike.isEmpty()) {
            throw new IllegalStateException("싫어요를 누르지 않았습니다.");
        }
        PostLike postLike = existingDislike.get();
        postLike.cancel();
        postStat.decreaseDislikeCount();

        likeRepository.save(postLike);
        statRepository.save(postStat);
    }

    public PostStat getPostStat(Long postId) {
        PostStat postStat = statRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));
        return postStat;
    }
}
