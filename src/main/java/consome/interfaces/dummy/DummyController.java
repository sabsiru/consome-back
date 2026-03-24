package consome.interfaces.dummy;

import consome.domain.dummy.DummyDataInsertService;
import consome.domain.post.PopularPostService;
import consome.domain.post.repository.PostRepository;
import consome.infrastructure.scheduler.BoardStatScheduler;
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
    private final BoardStatScheduler boardStatScheduler;
    private final PopularPostService popularPostService;
    private final PostRepository postRepository;

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

    @PostMapping("/boardInsert")
    public ResponseEntity<String> insertBoard(
            @RequestParam Long sectionId,
            @RequestParam(defaultValue = "10") int count) {
        dummyService.bulkInsertBoards(sectionId, count);
        return ResponseEntity.ok("Board insert complete: " + count + " boards");
    }

    @PostMapping("/refreshPopular")
    public ResponseEntity<String> refreshPopular() {
        // 1. 게시판 평균 통계 갱신
        boardStatScheduler.updateBoardStats();

        // 2. 전체 게시글에 대해 인기 점수 재계산
        var postIds = postRepository.findAll().stream()
                .map(post -> post.getId())
                .toList();

        int promoted = 0;
        for (Long postId : postIds) {
            popularPostService.updateScore(postId);
            promoted++;
        }

        return ResponseEntity.ok("Board stats refreshed + popular score updated for " + promoted + " posts");
    }
}
