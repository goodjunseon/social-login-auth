package com.junseon.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/*
 * @ConfigurationPropertiesScan 어노테이션은 Spring Boot 애플리케이션에서
 * @ConfigurationProperties로 주석이 달린 클래스를 자동으로 검색하고 등록하는 데 사용됩니다.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
