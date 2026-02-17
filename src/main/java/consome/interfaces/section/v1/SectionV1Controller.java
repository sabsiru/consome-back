package consome.interfaces.section.v1;

import consome.application.section.SectionFacade;
import consome.interfaces.admin.dto.section.SectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sections")
public class SectionV1Controller {

    private final SectionFacade sectionFacade;

    @GetMapping
    public ResponseEntity<List<SectionResponse>> findAll() {
        return ResponseEntity.ok(sectionFacade.findAllWithBoards());
    }
}
