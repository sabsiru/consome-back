package consome.domain.point;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUserId(Long userId);
}
