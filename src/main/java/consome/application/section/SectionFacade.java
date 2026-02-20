package consome.application.section;

import consome.domain.admin.Board;
import consome.domain.admin.Section;
import consome.domain.admin.SectionService;
import consome.interfaces.admin.dto.section.SectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionFacade {
    private final SectionService sectionService;

    public List<SectionResponse> findAllWithBoards() {
        List<Section> sections = sectionService.findAllOrdered();
        return sections.stream()
                .map(section -> {
                    List<Board> boards = sectionService.findBoardsBySectionId(section.getId());
                    return SectionResponse.from(section, boards);
                })
                .toList();
    }
}
