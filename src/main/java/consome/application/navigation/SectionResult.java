package consome.application.navigation;

import consome.domain.admin.Section;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.stream.Collectors;

public record SectionResult(
        Long refSectionId,
        String sectionName,
        int displayOrder,
        List<BoardResult> boards
) {
}
