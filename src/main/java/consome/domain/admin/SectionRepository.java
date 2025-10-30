package consome.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    boolean existsByName(String name);

    List<Section> findAllByDeletedFalseOrderByDisplayOrder();
}
