package com.inetum.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = "Influx API", version = "1.0", description = "Manage GET, POST, PUT and DELETE request to get and perform operations with data from InlfuxDB database"))
public class BackinfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackinfluxApplication.class, args);
	}

}
