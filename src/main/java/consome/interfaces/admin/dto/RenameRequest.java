package consome.interfaces.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RenameRequest {
    private Long id;
    private String name;
    private Long boardId;
}
