package consome.interfaces.admin;


import consome.application.admin.AdminFacade;
import consome.domain.board.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminFacade adminFacade;

    @PostMapping("/sections")
    public Section createSection(String name, int displayOrder) {
        return adminFacade.createSection(name, displayOrder);
    }
}
