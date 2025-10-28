package consome.infrastructure.config;

import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
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
        userFacade.register(command);

        User admin = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalStateException("Admin 생성 실패"));
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

        System.out.println("[ADMIN INIT] 관리자 계정 생성 완료 : ID=admin / PW=Admin!23");
    }
}

