package consome.domain.post.repository;

import consome.application.navigation.PopularPostResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PopularPostQueryRepository {

    List<PopularPostResult> findPopularPosts(int limit);

    Page<PopularPostResult> findPopularPosts(Pageable pageable);
}
