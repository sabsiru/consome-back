package consome.application.admin;

import consome.domain.admin.Board;
import consome.domain.admin.Section;
import consome.domain.admin.SectionOrder;
import consome.domain.admin.SectionService;
import consome.domain.common.exception.BusinessException;
import consome.domain.user.Role;
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

    private void validateAdminOnly(Role userRole) {
        if (userRole != Role.ADMIN) {
            throw new BusinessException("FORBIDDEN", "관리자만 접근할 수 있습니다.");
        }
    }

    @Transactional
    public SectionResponse create(String name, Role userRole) {
        validateAdminOnly(userRole);
        Section section = sectionService.create(name);
        return SectionResponse.from(section, List.of());
    }

    @Transactional
    public SectionResponse update(Long sectionId, String name, Role userRole) {
        validateAdminOnly(userRole);
        Section section = sectionService.update(sectionId, name);
        List<Board> boards = sectionService.findBoardsBySectionId(sectionId);
        return SectionResponse.from(section, boards);
    }

    @Transactional
    public void delete(Long sectionId, Role userRole) {
        validateAdminOnly(userRole);
        sectionService.delete(sectionId);
    }

    @Transactional
    public void reorder(List<SectionOrder> orders, Role userRole) {
        validateAdminOnly(userRole);
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
