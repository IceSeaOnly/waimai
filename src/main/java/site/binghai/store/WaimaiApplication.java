package site.binghai.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WaimaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaimaiApplication.class, args);
	}
}
