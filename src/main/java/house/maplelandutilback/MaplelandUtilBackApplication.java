package house.maplelandutilback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaplelandUtilBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaplelandUtilBackApplication.class, args);
	}
}
