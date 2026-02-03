package consome.application.navigation;

import consome.application.admin.AdminBoardFacade;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class NavigationFacadeIntegrationTest {

    @Autowired
    NavigationFacade mainFacade;

    @Autowired
    AdminBoardFacade adminBoardFacade;

}