package com.noralearn.goethehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GoetheHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoetheHubApplication.class, args);
	}
}
