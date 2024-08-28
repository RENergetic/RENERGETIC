package com.renergetic.ingestionapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.renergetic")
@EntityScan(basePackages = "com.renergetic")
@SpringBootApplication(scanBasePackages = "com.renergetic")
@ComponentScan(basePackages = "com.renergetic")
@OpenAPIDefinition(info = @Info(title = "Ingestion API", version = "1.0", description = "API to insert time-series data from third party systems"))
public class IngestionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionApiApplication.class, args);
	}

}
