package com.renergetic.measurementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.renergetic")
@EnableJpaRepositories(basePackages = "com.renergetic")
@EntityScan(basePackages = "com.renergetic")
@ComponentScan(basePackages = "com.renergetic")
@OpenAPIDefinition(info = @Info(title = "Measurement API", version = "1.0", description = "API to insert and retrieve time-series InfluxDB data"))
public class MeasuremenapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeasuremenapiApplication.class, args);
	}

}
