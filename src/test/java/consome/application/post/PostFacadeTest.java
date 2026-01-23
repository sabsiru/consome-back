package consome.application.post;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.post.PostService;
import consome.domain.post.entity.Post;
import consome.domain.post.repository.PostImageRepository;
import consome.domain.user.UserRepository;
import consome.infrastructure.storage.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
class PostFacadeTest {

    @Autowired
    private PostFacade postFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private PostService postService;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorage fileStorage;

    private Long userId;
    private long boardId = 1L;
    private long categoryId = 1L;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        UserRegisterCommand userRegisterCommand = UserRegisterCommand.of("testid", "테스트닉네임", "Password123");
        userId = userFacade.register(userRegisterCommand);
    }

    @DisplayName("이미지 포함 게시글 작성")
    @Test
    void createPost_V1_withImages_success() {
        // given
        String content = """
              <p>내용</p>
              <img data-image-index="0"/>
              """;
        PostCommand command = PostCommand.of(boardId, categoryId, userId, "제목", content);

        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "data".getBytes()
        );
        List<MultipartFile> images = List.of(image);

        // when
        PostResult result = postFacade.postV1(command, images);


        // then
        assertThat(result.postId()).isNotNull();
        assertThat(postService.getPost(result.postId()).getContent())
                .contains("/uploads/posts/");
        assertThat(postImageRepository.findByPostId(result.postId())).hasSize(1);
    }

    @DisplayName("게시글 수정 시 삭제된 이미지 제거")
    @Test
    void edit_removeDeletedImages() {
        // given - 이미지 2개로 게시글 작성
        String content = """
          <p>내용</p>
          <img data-image-index="0"/>
          <img data-image-index="1"/>
          """;
        PostCommand command = PostCommand.of(boardId, categoryId, userId, "제목", content);

        MockMultipartFile image1 = new MockMultipartFile("image", "test1.jpg", "image/jpeg", "data1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("image", "test2.jpg", "image/jpeg", "data2".getBytes());

        PostResult result = postFacade.postV1(command, List.of(image1, image2));
        Long postId = result.postId();

        // 저장된 이미지 URL 확인
        Post savedPost = postService.getPost(postId);
        List<String> savedUrls = fileStorage.extractImageUrls(savedPost.getContent());

        // when - 이미지 1개만 남기고 수정
        String newContent = "<p>수정됨</p><img src=\"" + savedUrls.get(0) + "\"/>";
        postFacade.edit(command.title(), command.categoryId(),newContent, postId, userId, null);

        // then
        assertThat(postImageRepository.findByPostId(postId)).hasSize(1);
    }

    @DisplayName("게시글 수정 시 이미지 유지")
    @Test
    void edit_keepAllImages() {
        // given
        String content = "<p>내용</p><img data-image-index=\"0\"/>";
        PostCommand command = PostCommand.of(boardId, categoryId, userId, "제목", content);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());

        PostResult result = postFacade.postV1(command, List.of(image));
        Long postId = result.postId();

        Post savedPost = postService.getPost(postId);
        String savedContent = savedPost.getContent();

        // when - 같은 이미지로 수정
        postFacade.edit(command.title(), command.categoryId(),content, postId, userId, null);

        // then
        assertThat(postImageRepository.findByPostId(postId)).hasSize(1);
    }

    @DisplayName("게시글 수정 시 이미지 전체 삭제")
    @Test
    void edit_removeAllImages() {
        // given
        String content = "<p>내용</p><img data-image-index=\"0\"/>";
        PostCommand command = PostCommand.of(boardId, categoryId, userId, "제목", content);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());

        PostResult result = postFacade.postV1(command, List.of(image));
        Long postId = result.postId();

        // when
        String newContent = "<p>수정됨</p>";
        postFacade.edit(command.title(), command.categoryId(),newContent, postId, userId, null);

        // then
        assertThat(postImageRepository.findByPostId(postId)).isEmpty();
    }

}