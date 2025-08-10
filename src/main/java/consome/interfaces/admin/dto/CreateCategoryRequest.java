package consome.interfaces.admin.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "게시판을 선택해 주세요")
    private Long boardId;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 1, max = 20, message = "20자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "순서를 입력해주세요.")
    private int displayOrder;
}
