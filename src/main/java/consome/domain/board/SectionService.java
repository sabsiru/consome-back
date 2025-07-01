package consome.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    public Section create(String name, int displayOrder) {
        Section section = Section.create(name, displayOrder);
        return sectionRepository.save(section);
    }

    public Section rename(Long sectionId, String newName) {
        Section section = findById(sectionId);
        section.rename(newName);
        return sectionRepository.save(section);
    }

    public Section changeOrder(Long sectionId, int newOrder) {
        Section section = findById(sectionId);
        section.changeOrder(newOrder);
        return sectionRepository.save(section);
    }

    public void delete(Long sectionId) {
        Section section = findById(sectionId);
        section.delete();
        sectionRepository.save(section);
    }

    public Section findById(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
    }
}
