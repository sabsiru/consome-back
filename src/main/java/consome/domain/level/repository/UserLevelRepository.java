package consome.domain.level.repository;

import consome.domain.level.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    @Query("SELECT ul FROM UserLevel ul WHERE ul.level = 20 ORDER BY ul.totalExp DESC")
    List<UserLevel> findAllLevel20UsersOrderByExpDesc();

    @Query("SELECT COUNT(ul) FROM UserLevel ul WHERE ul.level = 20")
    long countLevel20Users();
}
