package cn.intellif.fegin.intellif;

import cn.intellif.fegin.intellif.annotation.EnableFegin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFegin
@SpringBootApplication
public class IntellifFeginApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntellifFeginApplication.class, args);
	}
}
