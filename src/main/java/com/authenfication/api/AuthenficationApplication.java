package com.authenfication.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AuthenficationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenficationApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	//kakao와 통신을 위해 추가
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}
}
