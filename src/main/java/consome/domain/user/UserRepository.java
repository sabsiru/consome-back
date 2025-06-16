package consome.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByLoginId(String loginId);

    List<User> findByNickname(String nickname);
}
