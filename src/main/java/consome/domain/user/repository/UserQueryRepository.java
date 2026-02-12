package consome.domain.user.repository;

import consome.application.user.UserCommentResult;
import consome.application.user.UserPostResult;
import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchResult;
import consome.domain.user.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Page<UserInfo> findUsers(Pageable pageable);

    Page<UserSearchResult> search(UserSearchCommand command, Pageable pageable);

    Page<UserPostResult> findPostsByUserId(Long userId, Pageable pageable);

    Page<UserCommentResult> findCommentsByUserId(Long userId, Pageable pageable);

    int countPostsByUserId(Long userId);

    int countCommentsByUserId(Long userId);
}
