package consome.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    boolean existsByName(String name);
}
