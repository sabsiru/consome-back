package consome.domain.admin;

import consome.interfaces.admin.dto.SectionReorderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    public Section create(String name, int displayOrder) {
        isNameDuplicate(name);
        Section section = Section.create(name, displayOrder);
        return sectionRepository.save(section);
    }

    public Section rename(Long sectionId, String newName) {
        Section section = findById(sectionId);
        isNameDuplicate(newName);
        section.rename(newName);
        return sectionRepository.save(section);
    }

    public Section changeOrder(Long sectionId, int newOrder) {
        Section section = findById(sectionId);
        section.changeOrder(newOrder);
        return sectionRepository.save(section);
    }

    @Transactional
    public void reorder(List<SectionOrder> orders) {
        // 1. 기존 섹션들 전부 고유 임시값으로 변경
        for (Section s : sectionRepository.findAll()) {
            s.changeOrder(-s.getId().intValue()); // 임시 음수값 (-1, -2, -3, ...)
        }
        sectionRepository.flush(); // 임시값 반영

        // 2. 요청된 순서대로 업데이트
        for (SectionOrder order : orders) {
            Section section = sectionRepository.findById(order.sectionId())
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
            section.changeOrder(order.displayOrder());
        }

        sectionRepository.flush(); // 최종 순서 반영
    }

    public void delete(Long sectionId) {
        Section section = findById(sectionId);
        section.delete();
        sectionRepository.save(section);
    }

    public boolean isNameDuplicate(String name) {
        if (sectionRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 섹션 이름입니다.");
        }
        if (name == null || name.trim().isEmpty() || name.length() < 1 || name.length() > 10) {
            throw new IllegalArgumentException("섹션 이름은 1자 이상 10자 이하로 입력해야 합니다.");
        }
        return sectionRepository.existsByName(name);
    }

    public Section findById(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 접근입니다."));
    }

    public List<Section> findAllOrdered() {
        return sectionRepository.findAllByDeletedFalseOrderByDisplayOrder();
    }
}
