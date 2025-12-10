package consome.interfaces.dummy;

import consome.domain.dummy.DummyDataInsertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dummy")
public class DummyController {
    private final DummyDataInsertService dummyService;

    @PostMapping("/userInsert")
    public ResponseEntity<String> insertUser(@RequestParam(defaultValue = "1000") int count) {
        dummyService.bulkInsertDummyData(count);
        return ResponseEntity.ok("User insert dummy endpoint");
    }
}
