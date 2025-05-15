package consome;

import org.springframework.boot.SpringApplication;

public class TestConsomeApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsomeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
