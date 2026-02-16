package consome.infrastructure.config;

import consome.application.admin.AdminBoardFacade;
import consome.application.admin.AdminCategoryFacade;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.admin.Board;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitialize implements CommandLineRunner {

    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final AdminBoardFacade adminBoardFacade;
    private final AdminCategoryFacade adminCategoryFacade;

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

            Board board = adminBoardFacade.create("배틀그라운드", "서바이벌 게임의 유행 선두자 PUBG게시판 입니다.");
        }
}

