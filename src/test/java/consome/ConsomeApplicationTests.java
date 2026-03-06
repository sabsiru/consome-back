package consome;

import consome.domain.email.EmailVerificationService;
import consome.infrastructure.mail.EmailService;
import consome.infrastructure.redis.EmailVerificationRedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class ConsomeApplicationTests {

	@MockBean
	EmailService emailService;

	@MockBean
	EmailVerificationService emailVerificationService;

	@MockBean
	EmailVerificationRedisRepository emailVerificationRedisRepository;

	@Test
	void contextLoads() {
	}

}
