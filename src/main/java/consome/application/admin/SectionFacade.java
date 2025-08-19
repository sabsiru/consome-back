package consome.application.admin;


import consome.domain.board.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void delete(Long sectionId) {
        sectionService.delete(sectionId);
    }
}
