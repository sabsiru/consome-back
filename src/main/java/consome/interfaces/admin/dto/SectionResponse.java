package consome.interfaces.admin.dto;


import consome.domain.admin.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectionResponse {

    private Long id;
    private String name;
    private int displayOrder;

    public static SectionResponse from(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getName(),
                section.getDisplayOrder()
        );
    }
}
