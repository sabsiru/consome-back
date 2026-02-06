package consome.domain.post;

import consome.domain.admin.Board;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.post.entity.PopularPost;
import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;
import consome.domain.post.repository.PopularPostRepository;
import consome.domain.post.repository.PostRepository;
import consome.domain.post.repository.PostStatRepository;
import consome.infrastructure.redis.PopularPostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopularPostService {

    private static final double SCORE_THRESHOLD = 1.0;
    private static final int MIN_LIKES = 5;

    private static final double VIEW_WEIGHT = 0.1;
    private static final double LIKE_WEIGHT = 0.7;
    private static final double COMMENT_WEIGHT = 0.2;

    private final PopularPostRepository popularPostRepository;
    private final PopularPostRedisRepository popularPostRedisRepository;
    private final PostRepository postRepository;
    private final PostStatRepository postStatRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void updateScore(Long postId) {
        if (popularPostRepository.existsByPostId(postId)) {
            return;
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || post.isDeleted()) {
            return;
        }

        PostStat stat = postStatRepository.findById(postId).orElse(null);
        if (stat == null) {
            return;
        }

        Board board = boardRepository.findById(post.getBoardId()).orElse(null);
        if (board == null) {
            return;
        }

        double score = calculateScore(stat, board);
        popularPostRedisRepository.addCandidate(postId, score);

        if (score >= SCORE_THRESHOLD && stat.getLikeCount() >= MIN_LIKES) {
            PopularPost popularPost = PopularPost.create(
                    postId,
                    post.getBoardId(),
                    stat.getViewCount(),
                    stat.getLikeCount(),
                    stat.getCommentCount()
            );
            popularPostRepository.save(popularPost);
            popularPostRedisRepository.removeCandidate(postId);
        }
    }

    private double calculateScore(PostStat stat, Board board) {
        double relativeView = safeDiv(stat.getViewCount(), board.getAvgViewCount());
        double relativeLike = safeDiv(stat.getLikeCount(), board.getAvgLikeCount());
        double relativeComment = safeDiv(stat.getCommentCount(), board.getAvgCommentCount());

        return relativeView * VIEW_WEIGHT + relativeLike * LIKE_WEIGHT + relativeComment * COMMENT_WEIGHT;
    }

    private double safeDiv(int value, double avg) {
        return avg > 0 ? value / avg : 0;
    }
}
