package consome.infrastructure.config;

import consome.application.admin.AdminBoardFacade;
import consome.application.admin.AdminSectionFacade;
import consome.application.user.UserFacade;
import consome.application.user.UserRegisterCommand;
import consome.domain.admin.Board;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import consome.interfaces.admin.dto.section.SectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitialize implements CommandLineRunner {

    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final AdminBoardFacade adminBoardFacade;
    private final AdminSectionFacade adminSectionFacade;

    @Value("${app.admin.password:#{null}}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        String loginId = "admin";

        if (userRepository.existsByLoginId(loginId)) {
            System.out.println("[ADMIN INIT] 이미 관리자 계정이 존재합니다.");
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            System.out.println("[ADMIN INIT] app.admin.password 미설정 — 관리자 계정 생성 스킵");
            return;
        }

        UserRegisterCommand command = UserRegisterCommand.of(
                loginId,
                "관리자",
                adminPassword,
                "admin@consome.site"
        );

        UserRegisterCommand testUser = UserRegisterCommand.of(
                "test1",
                "테스트유저",
                "Test!234",
                "test1@test.com"
        );
        UserRegisterCommand testUser2 = UserRegisterCommand.of(
                "test2",
                "테스트유저2",
                "Test!234",
                "test2@test.com"
        );

        UserRegisterCommand testUser3 = UserRegisterCommand.of(
                "test3",
                "테스트유저3",
                "Test!234",
                "test3@test.com"
        );

        UserRegisterCommand testUser4 = UserRegisterCommand.of(
                "test4",
                "테스트유저4",
                "Test!234",
                "test4@test.com"
        );

        userFacade.registerWithoutEmail(command);
        userFacade.registerWithoutEmail(testUser);
        userFacade.registerWithoutEmail(testUser2);
        userFacade.registerWithoutEmail(testUser3);
        userFacade.registerWithoutEmail(testUser4);

        User admin = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalStateException("Admin 생성 실패"));
        admin.updateRole(Role.ADMIN);
        admin.verifyEmail();  // 관리자는 이메일 인증 완료
        userRepository.save(admin);

        // 테스트 유저들 이메일 인증 완료
        userRepository.findByLoginId("test1").ifPresent(User::verifyEmail);
        userRepository.findByLoginId("test2").ifPresent(User::verifyEmail);
        userRepository.findByLoginId("test3").ifPresent(User::verifyEmail);
        userRepository.findByLoginId("test4").ifPresent(User::verifyEmail);

        System.out.println("[ADMIN INIT] 관리자 계정 생성 완료");

        // 기본 섹션 및 게시판 생성
        SectionResponse gameSection = adminSectionFacade.create("게임", Role.ADMIN);
        adminBoardFacade.create("배틀그라운드", "서바이벌 게임의 유행 선두자 PUBG게시판 입니다.", gameSection.id(), Role.ADMIN);
        adminBoardFacade.create("랑그릿사 모바일", "페어리테일 시작", gameSection.id(), Role.ADMIN);
        adminBoardFacade.create("연운", "무협풍 오픈월드", gameSection.id(), Role.ADMIN);
    }
}

