package consome.domain.post;

import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostReaction;
import consome.domain.post.entity.PostStat;
import consome.domain.post.entity.PostView;
import consome.domain.post.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostStatRepository statRepository;
    private final PostReactionRepository likeRepository;
    private final PostViewRepository viewRepository;
    private final PostQueryRepository postQueryRepository;

    public Post post(long boardId, long categoryId, Long authorId, String title, String content) {
        Post post = Post.write(boardId, categoryId, authorId, title, content);
        PostStat stat = PostStat.init(post);
        postRepository.save(post);
        statRepository.save(stat);

        return post;
    }

    public Post edit(String content, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getRefUserId().equals(userId)) {
            throw new IllegalStateException("작성자만 게시글을 수정할 수 있습니다.");
        }

        post.edit(content);

        return post;
    }

    public Post delete(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getRefUserId().equals(userId)) {
            throw new IllegalStateException("작성자만 게시글을 삭제할 수 있습니다.");
        }
        post.delete();


        return postRepository.save(post);
    }

    public PostStat like(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());

        if (likeRepository.findByIdForUpdate(post.getId(), userId, ReactionType.LIKE).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }
        PostReaction postReaction = PostReaction.like(post.getId(), userId);
        postStat.increaseLikeCount();

        likeRepository.save(postReaction);

        return statRepository.save(postStat);
    }

    public PostStat dislike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());

        if (likeRepository.findByIdForUpdate(post.getId(), userId, ReactionType.DISLIKE).isPresent()) {
            throw new IllegalStateException("이미 싫어요를 누른 게시글입니다.");
        }
        PostReaction postReaction = PostReaction.disLike(post.getId(), userId);
        postStat.increaseDislikeCount();

        likeRepository.save(postReaction);
        return statRepository.save(postStat);
    }

    public PostStat cancelLike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());
        Optional<PostReaction> existingLike = likeRepository.findByIdForUpdate(post.getId(), userId, ReactionType.LIKE);
        if (likeRepository.findByIdForUpdate(post.getId(), userId, ReactionType.LIKE).isEmpty()) {
            throw new IllegalStateException("좋아요를 누르지 않았습니다.");
        }
        PostReaction postReaction = existingLike.get();
        postReaction.cancel();
        postStat.decreaseLikeCount();

        likeRepository.save(postReaction);
        return statRepository.save(postStat);
    }

    public PostStat cancelDislike(Post post, Long userId) {
        PostStat postStat = getPostStat(post.getId());
        Optional<PostReaction> existingDislike = likeRepository.findByIdForUpdate(post.getId(), userId, ReactionType.DISLIKE);
        if (existingDislike.isEmpty()) {
            throw new IllegalStateException("싫어요를 누르지 않았습니다.");
        }
        PostReaction postReaction = existingDislike.get();
        postReaction.cancel();
        postStat.decreaseDislikeCount();

        likeRepository.save(postReaction);
        return statRepository.save(postStat);
    }

    public PostStat increaseViewCount(Long postId, String userIp, Long userId) {
        PostStat postStat = getPostStat(postId);
        Optional<PostView> byPostIdAndUserIp = viewRepository.findByPostIdAndUserIdOrUserIp(postId, userIp, userId);
        if (byPostIdAndUserIp.isPresent()) {
            return postStat;
        }
        postStat.increaseViewCount();
        PostView postView = PostView.create(postId, userIp, userId);
        viewRepository.save(postView);
        return statRepository.save(postStat);

    }

    public PostStat increaseCommentCount(Long postId) {
        PostStat postStat = getPostStat(postId);
        postStat.increaseCommentCount();
        return statRepository.save(postStat);
    }

    public PostStat getPostStat(Long postId) {
        PostStat postStat = statRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));
        return postStat;
    }

    public Post getPost(Long postId) {
        return postRepository.findByPostIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public Page<PostSummary> getPostByBoard(Long boardId, Pageable pageable) {
        return postQueryRepository.findPostWithStatsByBoardId(boardId, pageable);
    }
}
