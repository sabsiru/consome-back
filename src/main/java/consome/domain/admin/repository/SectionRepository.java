package consome.domain.admin.repository;

import consome.domain.admin.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    boolean existsByName(String name);

    List<Section> findAllByOrderByDisplayOrderAsc();
}
