package consome;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestConsomeApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsomeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
