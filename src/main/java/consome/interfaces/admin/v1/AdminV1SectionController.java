package consome.interfaces.admin.v1;

import consome.application.admin.AdminSectionFacade;
import consome.domain.admin.SectionOrder;
import consome.interfaces.admin.dto.section.CreateSectionRequest;
import consome.interfaces.admin.dto.section.SectionReorderRequest;
import consome.interfaces.admin.dto.section.SectionResponse;
import consome.interfaces.admin.dto.section.UpdateSectionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/sections")
public class AdminV1SectionController {

    private final AdminSectionFacade adminSectionFacade;

    @GetMapping
    public ResponseEntity<List<SectionResponse>> findAll() {
        return ResponseEntity.ok(adminSectionFacade.findAll());
    }

    @PostMapping
    public ResponseEntity<SectionResponse> create(@RequestBody @Valid CreateSectionRequest request) {
        return ResponseEntity.ok(adminSectionFacade.create(request.name()));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> update(
            @PathVariable Long sectionId,
            @RequestBody @Valid UpdateSectionRequest request) {
        return ResponseEntity.ok(adminSectionFacade.update(sectionId, request.name()));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> delete(@PathVariable Long sectionId) {
        adminSectionFacade.delete(sectionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody SectionReorderRequest request) {
        List<SectionOrder> orders = request.orders().stream()
                .map(o -> new SectionOrder(o.sectionId(), o.displayOrder()))
                .toList();
        adminSectionFacade.reorder(orders);
        return ResponseEntity.ok().build();
    }
}
