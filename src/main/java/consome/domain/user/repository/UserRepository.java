package consome.domain.user.repository;

import consome.domain.user.User;
import consome.domain.user.SuspensionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.suspensionType IS NOT NULL AND u.suspensionType <> :permanent AND u.suspendedUntil < :now")
    List<User> findExpiredSuspensions(@Param("permanent") SuspensionType permanent, @Param("now") LocalDateTime now);
}
