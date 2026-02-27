package consome.interfaces.admin.v1;

import consome.application.admin.AdminSectionFacade;
import consome.domain.admin.SectionOrder;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.admin.dto.section.CreateSectionRequest;
import consome.interfaces.admin.dto.section.SectionReorderRequest;
import consome.interfaces.admin.dto.section.SectionResponse;
import consome.interfaces.admin.dto.section.UpdateSectionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<SectionResponse> create(@RequestBody @Valid CreateSectionRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(adminSectionFacade.create(request.name(), userDetails.getRole()));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> update(
            @PathVariable Long sectionId,
            @RequestBody @Valid UpdateSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(adminSectionFacade.update(sectionId, request.name(), userDetails.getRole()));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> delete(@PathVariable Long sectionId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminSectionFacade.delete(sectionId, userDetails.getRole());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody SectionReorderRequest request,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SectionOrder> orders = request.orders().stream()
                .map(o -> new SectionOrder(o.sectionId(), o.displayOrder()))
                .toList();
        adminSectionFacade.reorder(orders, userDetails.getRole());
        return ResponseEntity.ok().build();
    }
}
