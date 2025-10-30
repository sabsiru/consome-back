package consome.interfaces.navigation.dto;

import consome.application.navigation.SectionHeaderResult;
import consome.domain.admin.Section;

import java.util.List;
import java.util.stream.Collectors;

public record SectionHeaderResponse(
        Long refSectionId,
        String sectionName,
        int displayOrder
) {
    /** App 계층 결과 → Interfaces 응답 매핑 */
    public static SectionHeaderResponse from(SectionHeaderResult r) {
        return new SectionHeaderResponse(r.refSectionId(), r.sectionName(), r.displayOrder());
    }

    /** 도메인 엔티티 → Interfaces 응답 매핑(필요 시 사용) */
    public static SectionHeaderResponse from(Section s) {
        return new SectionHeaderResponse(s.getId(), s.getName(), s.getDisplayOrder());
    }

    public static List<SectionHeaderResponse> fromResults(List<SectionHeaderResult> results) {
        return results.stream().map(SectionHeaderResponse::from).collect(Collectors.toList());
    }

    public static List<SectionHeaderResponse> fromSections(List<Section> sections) {
        return sections.stream().map(SectionHeaderResponse::from).collect(Collectors.toList());
    }
}
