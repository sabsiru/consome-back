package consome.application.admin;

import consome.domain.admin.*;
import consome.domain.admin.repository.BoardManagerRepository;
import consome.domain.admin.repository.BoardRepository;
import consome.domain.user.Role;
import consome.domain.user.User;
import consome.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class AdminDashboardFacadeTest {

    @Autowired
    private BoardService boardService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AdminDashboardFacade adminDashboardFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardManagerRepository boardManagerRepository;

    @Nested
    @DisplayName("관리자 지정/해제 테스트")
    class ManagerTests {

        private User testUser;
        private Board testBoard;

        @BeforeEach
        void setUp() {
            boardManagerRepository.deleteAll();
            userRepository.deleteAll();
            boardRepository.deleteAll();

            testUser = userRepository.save(User.create("testuser", "테스트유저", "password123!"));
            testBoard = boardRepository.save(Board.create("테스트게시판", "테스트 게시판입니다", 1));
        }

        @Test
        @DisplayName("관리자 지정 시 User의 role이 MANAGER로 변경된다")
        void assignManager_changesUserRoleToManager() {
            // given
            assertThat(testUser.getRole()).isEqualTo(Role.USER);

            // when
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());

            // then
            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
            assertThat(updatedUser.getRole()).isEqualTo(Role.MANAGER);
        }

        @Test
        @DisplayName("관리자 지정 시 BoardManager 레코드가 생성된다")
        void assignManager_createsBoardManagerRecord() {
            // when
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());

            // then
            List<BoardManager> managers = boardManagerRepository.findByBoardId(testBoard.getId());
            assertThat(managers).hasSize(1);
            assertThat(managers.get(0).getUserId()).isEqualTo(testUser.getId());
            assertThat(managers.get(0).getBoardId()).isEqualTo(testBoard.getId());
        }

        @Test
        @DisplayName("이미 관리자인 게시판에 다시 지정하면 예외 발생")
        void assignManager_throwsExceptionIfAlreadyManager() {
            // given
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());

            // when & then
            assertThatThrownBy(() -> adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 해당 게시판의 관리자입니다");
        }

        @Test
        @DisplayName("관리자 해제 시 다른 게시판이 없으면 User의 role이 USER로 복원된다")
        void removeManager_restoresUserRoleToUserIfNoOtherBoards() {
            // given
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());
            assertThat(userRepository.findById(testUser.getId()).orElseThrow().getRole()).isEqualTo(Role.MANAGER);

            // when
            adminDashboardFacade.removeManager(testBoard.getId(), testUser.getId());

            // then
            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
            assertThat(updatedUser.getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("관리자 해제 시 다른 게시판이 있으면 User의 role이 MANAGER로 유지된다")
        void removeManager_keepsManagerRoleIfOtherBoardsExist() {
            // given
            Board anotherBoard = boardRepository.save(Board.create("다른게시판", "다른 게시판입니다", 2));
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());
            adminDashboardFacade.assignManager(anotherBoard.getId(), testUser.getId());

            // when
            adminDashboardFacade.removeManager(testBoard.getId(), testUser.getId());

            // then
            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
            assertThat(updatedUser.getRole()).isEqualTo(Role.MANAGER);
        }

        @Test
        @DisplayName("관리자 해제 시 BoardManager 레코드가 삭제된다")
        void removeManager_deletesBoardManagerRecord() {
            // given
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());
            assertThat(boardManagerRepository.findByBoardId(testBoard.getId())).hasSize(1);

            // when
            adminDashboardFacade.removeManager(testBoard.getId(), testUser.getId());

            // then
            List<BoardManager> managers = boardManagerRepository.findByBoardId(testBoard.getId());
            assertThat(managers).isEmpty();
        }

        @Test
        @DisplayName("관리자가 아닌 사용자를 해제하면 예외 발생")
        void removeManager_throwsExceptionIfNotManager() {
            // when & then
            assertThatThrownBy(() -> adminDashboardFacade.removeManager(testBoard.getId(), testUser.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("해당 게시판의 관리자가 아닙니다");
        }

        @Test
        @DisplayName("getManagersByBoard - 게시판 관리자 목록 조회")
        void getManagersByBoard_returnsManagerList() {
            // given
            User anotherUser = userRepository.save(User.create("another", "다른유저", "password123!"));
            adminDashboardFacade.assignManager(testBoard.getId(), testUser.getId());
            adminDashboardFacade.assignManager(testBoard.getId(), anotherUser.getId());

            // when
            List<ManagerResult> managers = adminDashboardFacade.getManagersByBoard(testBoard.getId());

            // then
            assertThat(managers).hasSize(2);
            assertThat(managers).extracting(ManagerResult::boardName).containsOnly("테스트게시판");
        }
    }

    @Nested
    @DisplayName("유저 목록 조회 테스트 - managedBoards 포함")
    class GetUsersWithManagedBoardsTests {

        @BeforeEach
        void setUp() {
            boardManagerRepository.deleteAll();
            userRepository.deleteAll();
            boardRepository.deleteAll();
        }

        @Test
        @DisplayName("유저 목록 조회 시 managedBoards가 포함된다")
        void getUsers_includesManagedBoards() {
            // given
            User user = userRepository.save(User.create("manager1", "매니저1", "password123!"));
            Board board1 = boardRepository.save(Board.create("게시판1", "게시판1 설명", 1));
            Board board2 = boardRepository.save(Board.create("게시판2", "게시판2 설명", 2));

            adminDashboardFacade.assignManager(board1.getId(), user.getId());
            adminDashboardFacade.assignManager(board2.getId(), user.getId());

            Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());

            // when
            UserPagingResult result = adminDashboardFacade.getUsers(pageable);

            // then
            assertThat(result.users()).isNotEmpty();
            UserRowResult managerUser = result.users().stream()
                    .filter(u -> u.userId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow();

            assertThat(managerUser.managedBoards()).hasSize(2);
            assertThat(managerUser.managedBoards())
                    .extracting(ManagedBoardInfo::boardName)
                    .containsExactlyInAnyOrder("게시판1", "게시판2");
        }

        @Test
        @DisplayName("관리 게시판이 없는 유저는 빈 리스트를 반환한다")
        void getUsers_returnsEmptyManagedBoardsForRegularUser() {
            // given
            User regularUser = userRepository.save(User.create("regular", "일반유저", "password123!"));
            Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());

            // when
            UserPagingResult result = adminDashboardFacade.getUsers(pageable);

            // then
            UserRowResult userResult = result.users().stream()
                    .filter(u -> u.userId().equals(regularUser.getId()))
                    .findFirst()
                    .orElseThrow();

            assertThat(userResult.managedBoards()).isEmpty();
        }
    }
}
