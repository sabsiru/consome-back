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
        dummyService.bulkInsertUsersWithPoint(count);
        return ResponseEntity.ok("User insert dummy endpoint");
    }

    @PostMapping("/postInsert")
    public ResponseEntity<String> insertPost(@RequestParam(defaultValue = "100") int count) {
        dummyService.bulkInsertPosts(count);
        return ResponseEntity.ok("Post insert dummy endpoint");
    }

    @PostMapping("/postStatInsert")
    public ResponseEntity<String> insertPostStat(@RequestParam int maxViews, int maxLikes) {
        dummyService.bulkUpdatePostStats(maxViews, maxLikes);
        return ResponseEntity.ok("PostStat insert dummy endpoint");
    }
}
