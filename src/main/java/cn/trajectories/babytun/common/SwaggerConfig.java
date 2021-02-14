package cn.trajectories.babytun.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket userApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("简单的秒杀系统")
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
	// 预览地址:swagger-ui.html
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("简单的秒杀系统")
				.description("API 文档描述")
				.termsOfServiceUrl("http://www.trajectories.cn")
				.version("1.0.0")
				.build();
	}
}
