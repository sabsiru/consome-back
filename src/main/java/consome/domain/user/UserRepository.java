package consome.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByLoginId(String loginId);

    List<User> findAllByNickname(String nickname);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByLoginIdAndPassword(String loginId, String password);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);
}
