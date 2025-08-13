package consome.interfaces.admin.v1;


import consome.application.admin.SectionFacade;
import consome.domain.board.Section;
import consome.interfaces.admin.dto.ChangeOrderRequest;
import consome.interfaces.admin.dto.CreateSectionRequest;
import consome.interfaces.admin.dto.RenameRequest;
import consome.interfaces.admin.dto.SectionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/sections")
public class AdminV1SectionController {

    private final SectionFacade sectionFacade;

    @PostMapping()
    public SectionResponse create(@RequestBody @Valid CreateSectionRequest request) {
        Section section = sectionFacade.create(request.getName(), request.getDisplayOrder());
        return SectionResponse.from(section);
    }

    @PatchMapping("/{sectionId}/name")
    public SectionResponse rename(@PathVariable Long sectionId, @RequestBody RenameRequest request) {
        Section section = sectionFacade.rename(sectionId, request.getNewName());
        return SectionResponse.from(section);
    }

    @PatchMapping("/{sectionId}/order")
    public SectionResponse changeOrder(@PathVariable Long sectionId, @RequestBody ChangeOrderRequest request) {
        Section section = sectionFacade.changeOrder(sectionId, request.getNewOrder());
        return SectionResponse.from(section);
    }

    @DeleteMapping("/{sectionId}")
    public void delete(@PathVariable Long sectionId) {
        sectionFacade.delete(sectionId);
    }

}