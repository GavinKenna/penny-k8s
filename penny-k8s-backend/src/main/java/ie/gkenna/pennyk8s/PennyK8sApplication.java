package ie.gkenna.pennyk8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PennyK8sApplication {

	public static void main(String[] args) {
		SpringApplication.run(PennyK8sApplication.class, args);
	}

}
