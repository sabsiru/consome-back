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
    private final PostViewRepository viewRepository;

    public Post write(Post post) {
        PostStat stat = PostStat.init(post);
        postRepository.save(post);
        statRepository.save(stat);

        return post;
    }

    public Post edit(String title, String content, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        // 게시글 작성자와 현재 사용자가 일치하는지 확인
        if (!post.getAuthorId().equals(userId)) {
            throw new IllegalStateException("작성자만 게시글을 수정할 수 있습니다.");
        }
        post.edit(title, content);
        postRepository.save(post);

        return post;
    }

    public void delete(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if(!post.getAuthorId().equals(userId)){
            throw new IllegalStateException("작성자만 게시글을 삭제할 수 있습니다.");
        }
        post.delete();
        postRepository.save(post);
    }

    public void like(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());


        if (likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.LIKE).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }
        PostLike postLike = PostLike.like(post.getId(), userId);
        postStat.increaseLikeCount();

        likeRepository.save(postLike);
        statRepository.save(postStat);
    }

    public void dislike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());

        if (likeRepository.findByIdForUpdate(post.getId(), userId, LikeType.DISLIKE).isPresent()) {
            throw new IllegalStateException("이미 싫어요를 누른 게시글입니다.");
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

    public void increaseViewCount(Long postId, Long userId, String userIp) {
        PostStat postStat = getPostStat(postId);
        Optional<PostView> byPostIdAndUserIp = viewRepository.findByPostIdAndUserIdOrUserIp(postId, userId, userIp);
        if (byPostIdAndUserIp.isPresent()) {
            return;
        }
        postStat.increaseViewCount();
        PostView postView = PostView.create(postId, userIp, userId);
        viewRepository.save(postView);
        statRepository.save(postStat);

    }

    public PostStat getPostStat(Long postId) {
        PostStat postStat = statRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));
        return postStat;
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }
}
