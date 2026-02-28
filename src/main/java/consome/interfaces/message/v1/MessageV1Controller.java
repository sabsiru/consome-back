package consome.interfaces.message.v1;

import consome.application.message.MessageFacade;
import consome.interfaces.message.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageV1Controller {

    private final MessageFacade messageFacade;

    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @RequestParam Long userId,
            @RequestBody @Valid SendMessageRequest request) {
        var result = messageFacade.send(request.toCommand(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.from(result));
    }

    @GetMapping("/received")
    public ResponseEntity<Page<MessageListResponse>> getReceivedMessages(
            @RequestParam Long userId,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = messageFacade.getReceivedMessages(userId, pageable);
        return ResponseEntity.ok(result.map(MessageListResponse::from));
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<MessageListResponse>> getSentMessages(
            @RequestParam Long userId,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = messageFacade.getSentMessages(userId, pageable);
        return ResponseEntity.ok(result.map(MessageListResponse::from));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> readMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        var result = messageFacade.readMessage(messageId, userId);
        return ResponseEntity.ok(MessageResponse.from(result));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        messageFacade.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@RequestParam Long userId) {
        long count = messageFacade.getUnreadCount(userId);
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PostMapping("/blocks/{blockedId}")
    public ResponseEntity<BlockResponse> block(
            @RequestParam Long userId,
            @PathVariable Long blockedId) {
        var result = messageFacade.block(userId, blockedId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BlockResponse.from(result));
    }

    @DeleteMapping("/blocks/{blockedId}")
    public ResponseEntity<Void> unblock(
            @RequestParam Long userId,
            @PathVariable Long blockedId) {
        messageFacade.unblock(userId, blockedId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocks")
    public ResponseEntity<Page<BlockResponse>> getBlockList(
            @RequestParam Long userId,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = messageFacade.getBlockList(userId, pageable);
        return ResponseEntity.ok(result.map(BlockResponse::from));
    }
}
