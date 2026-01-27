package consome.interfaces.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBoardRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 1, max = 20, message = "20자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "설명을 입력해주세요.")
    @Size(min = 1, max = 50, message = "50자 이하로 입력해주세요.")
    private String description;

    @NotNull(message = "순서를 입력해주세요.")
    private int displayOrder;
}
