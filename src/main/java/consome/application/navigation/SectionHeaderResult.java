package consome.application.navigation;

import consome.domain.board.Section;

import java.util.List;
import java.util.stream.Collectors;

public record SectionHeaderResult(
        Long refSectionId,
        String sectionName,
        int displayOrder
) {
    /** 도메인 엔티티 → App 결과 매핑 */
    public static SectionHeaderResult from(Section s) {
        return new SectionHeaderResult(s.getId(), s.getName(), s.getDisplayOrder());
    }

    /** 도메인 엔티티 리스트 → App 결과 리스트 매핑 */
    public static List<SectionHeaderResult> fromSections(List<Section> sections) {
        return sections.stream().map(SectionHeaderResult::from).collect(Collectors.toList());
    }
}
