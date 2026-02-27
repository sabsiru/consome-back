package consome.domain.dummy;


import consome.domain.admin.Board;
import consome.domain.admin.Category;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.admin.repository.CategoryRepository;
import consome.domain.point.Point;
import consome.domain.post.entity.Post;
import consome.domain.post.entity.PostStat;
import consome.domain.post.repository.PostRepository;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.instancio.Instancio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
public class DummyDataInsertService {

    private final EntityManagerFactory emf;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public void bulkInsertDummyData(int count) {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();

        for (int i = 0; i < count; i++) {
            int finalI = i;
            int finalI1 = i;
            User user = Instancio.of(User.class)
                    .supply(field("loginId"), () -> "login-" + finalI)
                    .supply(field("nickname"), () -> "nick" + finalI1)
                    .supply(field("password"), () -> "password123")
                    .supply(field("role"), () -> consome.domain.user.Role.USER)
                    .create();

            session.insert(user);

            if (i % 1000 == 0) {
                System.out.println("Inserted: " + i);
            }
        }
        tx.commit();
        session.close();

    }

    public void bulkInsertUsersWithPoint(int count) {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();

        for (int i = 0; i < count; i++) {
            int idx = i;
            User user = Instancio.of(User.class)
                    .ignore(field("id"))
                    .supply(field("loginId"), () -> "user" + idx)
                    .supply(field("nickname"), () -> "닉네임" + idx)
                    .supply(field("password"), () -> "Password123")
                    .supply(field("role"), () -> Role.USER)
                    .create();

            session.insert(user);

            Point point = Point.initialize(user.getId());
            session.insert(point);

            if (i % 1000 == 0) {
                System.out.println("Inserted: " + i);
            }
        }
        tx.commit();
        session.close();
    }

    public void bulkInsertPosts(int count) {
        List<Long> boardIds = boardRepository.findAll().stream()
                .map(Board::getId)
                .toList();

        List<Long> userIds = userRepository.findAll().stream()
                .map(User::getId)
                .toList();

        List<Long> categoryIds = categoryRepository.findAll().stream()
                .map(Category::getId)
                .toList();

        if (boardIds.isEmpty()) {
            throw new IllegalStateException("게시판이 없습니다.");
        }

        Random random = new Random();

        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();


        for (int i = 0; i < count; i++) {
            int idx = i;
            Long boardId = boardIds.get(random.nextInt(boardIds.size()));
            Long userId = userIds.get(random.nextInt(userIds.size()));
            Long categoryId = categoryIds.get(random.nextInt(categoryIds.size()));
            Post post = Instancio.of(Post.class)
                    .ignore(field("id"))
                    .supply(field("boardId"), () -> boardId)
                    .supply(field("categoryId"), () -> categoryId)
                    .supply(field("userId"), () -> userId)
                    .supply(field("title"), () -> "테스트 게시글 " + idx)
                    .supply(field("content"), () -> "테스트 내용 " + idx)
                    .supply(field("createdAt"), () -> LocalDateTime.now())
                    .supply(field("updatedAt"), () -> LocalDateTime.now())
                    .supply(field("deleted"), () -> false)
                    .supply(field("isPinned"), () -> false)
                    .create();

            session.insert(post);

            session.createNativeMutationQuery(
                    "INSERT INTO post_stat (post_id, view_count, like_count, dislike_count, comment_count) VALUES (:postId, 0, 0, 0, 0)")
                    .setParameter("postId", post.getId())
                    .executeUpdate();

            if (i % 1000 == 0) {
                System.out.println("Inserted posts: " + i);
            }
        }
        tx.commit();
        session.close();
    }

    public void bulkUpdatePostStats(int maxViews, int maxLikes) {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();

        List<Long> postIds = postRepository.findAll().stream()
                .map(Post::getId)
                .toList();

        Random random = new Random();

        for (Long postId : postIds) {
            int viewCount = random.nextInt(maxViews + 1);
            int likeCount = random.nextInt(maxLikes + 1);

            session.createNativeMutationQuery(
                    "UPDATE post_stat SET view_count = :viewCount, like_count = :likeCount WHERE post_id = :postId")
                    .setParameter("viewCount", viewCount)
                    .setParameter("likeCount", likeCount)
                    .setParameter("postId", postId)
                    .executeUpdate();
        }

        tx.commit();
        session.close();
    }
}
