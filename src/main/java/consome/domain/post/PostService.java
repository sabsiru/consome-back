package consome.domain.post;

import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostReaction;
import consome.domain.post.entity.PostStat;
import consome.domain.post.entity.PostView;
import consome.domain.post.exception.PostException;
import consome.domain.post.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostStatRepository statRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostViewRepository viewRepository;
    private final PostQueryRepository postQueryRepository;

    public Post post(Long boardId, Long categoryId, Long authorId, String title, String content) {
        Post post = Post.post(boardId, categoryId, authorId, title, content);
        PostStat stat = PostStat.init(post);
        postRepository.save(post);
        statRepository.save(stat);

        return post;
    }

    public Post edit(String title, Long categoryId, String content, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException.NotFound(postId));
        if (!post.getUserId().equals(userId)) {
            throw new PostException.Unauthorized("수정");
        }

        post.edit(title, categoryId, content);

        return post;
    }

    public Post delete(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException.NotFound(postId));
        if (!post.getUserId().equals(userId)) {
            throw new PostException.Unauthorized("삭제");
        }
        post.delete();


        return postRepository.save(post);
    }

    @Transactional
    public PostStat like(Post post, Long userId) {
        PostStat postStat = getPostStatForUpdate(post.getId());

        if (postReactionRepository.findByIdForUpdate(post.getId(), userId, ReactionType.LIKE).isPresent()) {
            throw new PostException.AlreadyLiked();
        }
        PostReaction postReaction = PostReaction.like(post.getId(), userId);
        postStat.increaseLikeCount();

        postReactionRepository.save(postReaction);

        return statRepository.save(postStat);
    }

    @Transactional
    public PostStat dislike(Post post, Long userId) {
        PostStat postStat = getPostStatForUpdate(post.getId());

        if (postReactionRepository.findByIdForUpdate(post.getId(), userId, ReactionType.DISLIKE).isPresent()) {
            throw new PostException.AlreadyDisliked();
        }
        PostReaction postReaction = PostReaction.disLike(post.getId(), userId);
        postStat.increaseDislikeCount();

        postReactionRepository.save(postReaction);
        return statRepository.save(postStat);
    }

    @Transactional
    public PostStat cancelLike(Post post, Long userId) {
        PostStat postStat = getPostStatForUpdate(post.getId());
        Optional<PostReaction> existingLike = postReactionRepository.findByIdForUpdate(post.getId(), userId, ReactionType.LIKE);
        if (existingLike.isEmpty()) {
            throw new PostException.NotLiked();
        }
        PostReaction postReaction = existingLike.get();
        postReaction.cancel();
        postStat.decreaseLikeCount();

        postReactionRepository.save(postReaction);
        return statRepository.save(postStat);
    }

    @Transactional
    public PostStat cancelDislike(Post post, Long userId) {
        PostStat postStat = getPostStatForUpdate(post.getId());
        Optional<PostReaction> existingDislike = postReactionRepository.findByIdForUpdate(post.getId(), userId, ReactionType.DISLIKE);
        if (existingDislike.isEmpty()) {
            throw new PostException.NotDisliked();
        }
        PostReaction postReaction = existingDislike.get();
        postReaction.cancel();
        postStat.decreaseDislikeCount();

        postReactionRepository.save(postReaction);
        return statRepository.save(postStat);
    }

    @Transactional
    public PostStat increaseViewCount(Long postId, String userIp, Long userId) {
        PostStat postStat = getPostStatForUpdate(postId);
        Optional<PostView> byPostIdAndUserIp = viewRepository.findByPostIdAndUserIdOrUserIp(postId, userIp, userId);
        if (byPostIdAndUserIp.isPresent()) {
            return postStat;
        }
        postStat.increaseViewCount();
        PostView postView = PostView.create(postId, userIp, userId);
        viewRepository.save(postView);
        return statRepository.save(postStat);

    }

    @Transactional
    public PostStat increaseCommentCount(Long postId) {
        PostStat postStat = getPostStatForUpdate(postId);
        postStat.increaseCommentCount();
        return statRepository.save(postStat);
    }

    public PostStat getPostStat(Long postId) {
        return statRepository.findById(postId)
                .orElseThrow(() -> new PostException.NotFound(postId));
    }

    public PostStat getPostStatForUpdate(Long postId) {
        return statRepository.findByPostIdForUpdate(postId)
                .orElseThrow(() -> new PostException.NotFound(postId));
    }

    public Page<PostSummary> findBoardPosts(Long boardId, Pageable pageable, Long categoryId) {
        return postQueryRepository.findPostWithStatsByBoardId(boardId, pageable, categoryId);
    }

    public Post getPost(Long postId) {
        return postRepository.findByPostIdAndDeletedFalse(postId)
                .orElseThrow(() -> new PostException.NotFound(postId));
    }

    public Page<PostSummary> getPostByBoard(Long boardId, Pageable pageable, Long categoryId) {
        return postQueryRepository.findPostWithStatsByBoardId(boardId, pageable, categoryId);
    }

    public Post getPostForUpdate(Long postId) {
        return postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new PostException.NotFound(postId));
    }

    public Page<PostSummary> searchPosts(Long boardId, String keyword, String searchType, Pageable pageable) {
        return postQueryRepository.searchPosts(boardId, keyword, searchType, pageable);
    }

    public Optional<Integer> getMaxPinnedOrder(Long boardId) {
        return postQueryRepository.findMaxPinnedOrderByBoardId(boardId);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public List<Post> findPinnedPosts(Long boardId) {
        return postRepository.findByBoardIdAndIsPinnedTrue(boardId);
    }

    @Transactional
    public void reorderPinnedPosts(Long boardId, List<PinnedPostOrder> orders) {
        List<Post> pinnedPosts = postRepository.findByBoardIdAndIsPinnedTrue(boardId);

        // 1️⃣ 임시 음수화 → flush
        for (Post post : pinnedPosts) {
            Integer currentOrder = post.getPinnedOrder();
            if (currentOrder != null) {
                post.updatePinnedOrder(-currentOrder);
            }
        }
        postRepository.flush();

        // 2️⃣ 실제 순서 반영 → flush
        Map<Long, Post> postMap = pinnedPosts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        for (PinnedPostOrder order : orders) {
            Post post = postMap.get(order.postId());
            if (post != null) {
                post.updatePinnedOrder(order.pinnedOrder());
            }
        }
        postRepository.flush();
    }

    public record PinnedPostOrder(Long postId, Integer pinnedOrder) {}
}
