package consome.domain.user;

import consome.application.user.UserSearchCommand;
import consome.application.user.UserSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Page<UserInfo> findUsers(Pageable pageable);

    Page<UserSearchResult> search(UserSearchCommand command, Pageable pageable);
}
