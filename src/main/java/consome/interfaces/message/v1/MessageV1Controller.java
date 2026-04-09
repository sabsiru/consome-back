package consome.interfaces.message.v1;

import consome.application.message.MessageFacade;
import consome.infrastructure.aop.RateLimit;
import consome.infrastructure.aop.RequireEmailVerified;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.message.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageV1Controller {

    private final MessageFacade messageFacade;

    @PostMapping
    @RequireEmailVerified
    @RateLimit(key = "message", limit = 10)
    public ResponseEntity<MessageResponse> send(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid SendMessageRequest request) {
        var result = messageFacade.send(request.toCommand(userDetails.getUserId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.from(result));
    }

    @GetMapping("/received")
    public ResponseEntity<Page<MessageListResponse>> getReceivedMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = messageFacade.getReceivedMessages(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(result.map(MessageListResponse::from));
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<MessageListResponse>> getSentMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = messageFacade.getSentMessages(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(result.map(MessageListResponse::from));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> readMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var result = messageFacade.readMessage(messageId, userDetails.getUserId());
        return ResponseEntity.ok(MessageResponse.from(result));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        messageFacade.deleteMessage(messageId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = messageFacade.getUnreadCount(userDetails.getUserId());
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PostMapping("/blocks/{blockedId}")
    public ResponseEntity<BlockResponse> block(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long blockedId) {
        var result = messageFacade.block(userDetails.getUserId(), blockedId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BlockResponse.from(result));
    }

    @DeleteMapping("/blocks/{blockedId}")
    public ResponseEntity<Void> unblock(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long blockedId) {
        messageFacade.unblock(userDetails.getUserId(), blockedId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blocks")
    public ResponseEntity<Page<BlockResponse>> getBlockList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 50) Pageable pageable) {
        var result = messageFacade.getBlockList(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(result.map(BlockResponse::from));
    }
}
