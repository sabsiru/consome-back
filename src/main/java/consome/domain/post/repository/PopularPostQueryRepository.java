package consome.domain.post.repository;

import consome.application.navigation.PopularPostResult;

import java.util.List;

public interface PopularPostQueryRepository {

    List<PopularPostResult> findPopularPosts(int limit);
}
