package consome.interfaces.post.v1;

import consome.application.post.PostFacade;
import consome.application.post.PostResult;
import consome.interfaces.post.dto.PostRequest;
import consome.interfaces.post.dto.PostResponse;
import consome.interfaces.post.mapper.PostRequestMapper;
import consome.interfaces.post.mapper.PostResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostV1Controller {

    private final PostFacade postFacade;

    @PostMapping
    public ResponseEntity<PostResponse> post(@RequestBody @Valid PostRequest request) {
        PostResult result = postFacade.post(PostRequestMapper.toPostCommand(request));
        PostResponse response = PostResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
