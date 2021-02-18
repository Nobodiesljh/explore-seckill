package cn.trajectories.babytun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("cn.trajectories.babytun")
@EnableCaching //开启声明式缓存，利用注解来控制缓存的读写
@EnableScheduling //启动任务调度功能
public class BabytunApplication {

	public static void main(String[] args) {
		SpringApplication.run(BabytunApplication.class, args);
	}

}
