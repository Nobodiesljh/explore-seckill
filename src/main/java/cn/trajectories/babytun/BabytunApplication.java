package cn.trajectories.babytun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.trajectories.babytun")
public class BabytunApplication {

	public static void main(String[] args) {
		SpringApplication.run(BabytunApplication.class, args);
	}

}
