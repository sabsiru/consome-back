package consome.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Page<UserInfo> findUsers(Pageable pageable);
}
