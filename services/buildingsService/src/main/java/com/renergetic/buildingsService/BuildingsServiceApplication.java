package com.renergetic.buildingsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Buildings API", version = "1.0", description = "Manage GET, POST, PUT and DELETE request to get or add buildings from PostgreSQL database"))
public class BuildingsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildingsServiceApplication.class, args);
	}

}
