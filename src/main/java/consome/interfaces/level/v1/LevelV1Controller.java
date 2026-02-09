package consome.interfaces.level.v1;

import consome.application.level.LevelFacade;
import consome.application.level.LevelResult;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.level.dto.LevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class LevelV1Controller {

    private final LevelFacade levelFacade;

    @GetMapping("/me/level")
    public ResponseEntity<LevelResponse> getMyLevel(@AuthenticationPrincipal CustomUserDetails userDetails) {
        LevelResult result = levelFacade.getMyLevel(userDetails.getUserId());
        return ResponseEntity.ok(LevelResponse.from(result));
    }

    @GetMapping("/{userId}/level")
    public ResponseEntity<LevelResponse> getUserLevel(@PathVariable Long userId) {
        LevelResult result = levelFacade.getUserLevel(userId);
        return ResponseEntity.ok(LevelResponse.from(result));
    }
}
