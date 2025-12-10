package consome.application.admin;


import consome.domain.admin.*;
import consome.interfaces.admin.dto.SectionReorderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionFacade {

    private final SectionService sectionService;

    public Section create(String name, int displayOrder) {
        return sectionService.create(name, displayOrder);
    }

    public Section rename(Long sectionId, String newName) {
        return sectionService.rename(sectionId, newName);
    }

    public Section changeOrder(Long sectionId, int newOrder) {
        return sectionService.changeOrder(sectionId, newOrder);
    }

    @Transactional
    public void reorder(List<SectionOrder> orders) {
        sectionService.reorder(orders);
    }

    public void delete(Long sectionId) {
        sectionService.delete(sectionId);
    }
}
