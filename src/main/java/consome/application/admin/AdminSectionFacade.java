package consome.application.admin;

import consome.domain.admin.Board;
import consome.domain.admin.Section;
import consome.domain.admin.SectionOrder;
import consome.domain.admin.SectionService;
import consome.interfaces.admin.dto.section.SectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSectionFacade {
    private final SectionService sectionService;

    @Transactional
    public SectionResponse create(String name, boolean adminOnly) {
        Section section = sectionService.create(name, adminOnly);
        return SectionResponse.from(section, List.of());
    }

    @Transactional
    public SectionResponse update(Long sectionId, String name, boolean adminOnly) {
        Section section = sectionService.update(sectionId, name, adminOnly);
        List<Board> boards = sectionService.findBoardsBySectionId(sectionId);
        return SectionResponse.from(section, boards);
    }

    @Transactional
    public void delete(Long sectionId) {
        sectionService.delete(sectionId);
    }

    @Transactional
    public void reorder(List<SectionOrder> orders) {
        sectionService.reorder(orders);
    }

    public List<SectionResponse> findAll() {
        List<Section> sections = sectionService.findAllOrdered();
        return sections.stream()
                .map(section -> {
                    List<Board> boards = sectionService.findBoardsBySectionId(section.getId());
                    return SectionResponse.from(section, boards);
                })
                .toList();
    }
}
