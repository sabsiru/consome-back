package consome.domain.admin;

import consome.domain.admin.repository.BoardRepository;
import consome.domain.admin.repository.SectionRepository;
import consome.domain.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final BoardRepository boardRepository;

    public Section create(String name) {
        validateName(name);
        if (sectionRepository.existsByName(name)) {
            throw new BusinessException("SECTION_DUPLICATE_NAME", "이미 존재하는 섹션 이름입니다.");
        }

        int maxOrder = sectionRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .mapToInt(Section::getDisplayOrder)
                .max()
                .orElse(0);

        Section section = Section.create(name);
        section.changeOrder(maxOrder + 1);
        return sectionRepository.save(section);
    }

    public Section update(Long sectionId, String name) {
        Section section = findById(sectionId);
        if (name != null && !name.equals(section.getName())) {
            validateName(name);
            if (sectionRepository.existsByName(name)) {
                throw new BusinessException("SECTION_DUPLICATE_NAME", "이미 존재하는 섹션 이름입니다.");
            }
            section.rename(name);
        }
        return sectionRepository.save(section);
    }

    public void delete(Long sectionId) {
        Section section = findById(sectionId);
        if (boardRepository.existsBySectionIdAndDeletedFalse(sectionId)) {
            throw new BusinessException("SECTION_HAS_BOARDS", "소속 게시판이 있어 삭제할 수 없습니다.");
        }
        sectionRepository.delete(section);
    }

    @Transactional
    public void reorder(List<SectionOrder> orders) {
        // 임시 음수화
        List<Section> sections = sectionRepository.findAllByOrderByDisplayOrderAsc();
        for (Section section : sections) {
            section.changeOrder(-section.getDisplayOrder() - 1);
        }
        sectionRepository.flush();

        // 실제 순서 반영
        for (SectionOrder order : orders) {
            Section section = sectionRepository.findById(order.sectionId())
                    .orElseThrow(() -> new BusinessException("SECTION_NOT_FOUND", "섹션을 찾을 수 없습니다."));
            section.changeOrder(order.displayOrder());
        }
        sectionRepository.flush();
    }

    public Section findById(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new BusinessException("SECTION_NOT_FOUND", "섹션을 찾을 수 없습니다."));
    }

    public List<Section> findAllOrdered() {
        return sectionRepository.findAllByOrderByDisplayOrderAsc();
    }

    public List<Board> findBoardsBySectionId(Long sectionId) {
        return boardRepository.findBySectionIdAndDeletedFalseOrderByCreatedAtAsc(sectionId);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 20) {
            throw new BusinessException("SECTION_INVALID_NAME", "섹션 이름은 1자 이상 20자 이하로 입력해야 합니다.");
        }
    }
}
