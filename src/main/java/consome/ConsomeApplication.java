package consome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ConsomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsomeApplication.class, args);
	}

}
