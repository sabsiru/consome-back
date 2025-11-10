package consome.interfaces.admin.v1;


import consome.application.admin.SectionFacade;
import consome.domain.admin.Section;
import consome.domain.admin.SectionOrder;
import consome.interfaces.admin.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody SectionReorderRequest request) {
        List<SectionOrder> orders = request.orders().stream()
                .map(o -> new SectionOrder(o.sectionId(), o.displayOrder()))
                .toList();

        sectionFacade.reorder(orders);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sectionId}")
    public void delete(@PathVariable Long sectionId) {
        sectionFacade.delete(sectionId);
    }

}