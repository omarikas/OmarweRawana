package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableFeignClients
@EnableCaching
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo.repo")
@EnableRedisRepositories(basePackages = "com.example.demo.model")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
