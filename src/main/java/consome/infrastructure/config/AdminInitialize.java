package consome.infrastructure.config;

import consome.application.admin.ManageBoardFacade;
import consome.application.admin.CategoryFacade;
import consome.application.admin.SectionFacade;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.admin.*;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitialize implements CommandLineRunner {

    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;

    private final SectionFacade sectionFacade;
    private final ManageBoardFacade manageBoardFacade;
    private final CategoryFacade categoryFacade;

    @Override
    public void run(String... args) {
        String loginId = "admin";

        if (userRepository.existsByLoginId(loginId)) {
            System.out.println("[ADMIN INIT] 이미 관리자 계정이 존재합니다.");
            return;
        }

        UserRegisterCommand command = UserRegisterCommand.of(
                loginId,
                "관리자",
                "Admin!23"
        );

        UserRegisterCommand testUser = UserRegisterCommand.of(
                "test1",
                "테스트",
                "Test!234"
        );
        UserRegisterCommand testUser2 = UserRegisterCommand.of(
                "test2",
                "테스트2",
                "Test!234"
        );

        userFacade.register(command);
        userFacade.register(testUser);
        userFacade.register(testUser2);

        User admin = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalStateException("Admin 생성 실패"));
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

        System.out.println("[ADMIN INIT] 관리자 계정 생성 완료 : ID=admin / PW=Admin!23");

        if(!sectionRepository.existsAllBy()){
            Section section = sectionFacade.create("자유", 1);
            Board board = manageBoardFacade.create(section.getId(), "자유게시판", "자유 게시판 입니다.", 1);
            categoryFacade.create(board.getId(), "잡담", 1);
        }
    }
}

