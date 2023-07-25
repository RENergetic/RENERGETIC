package com.renergetic.kpiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.renergetic")
@EnableJpaRepositories(basePackages = "com.renergetic")
@EntityScan(basePackages = "com.renergetic")
@ComponentScan(basePackages = "com.renergetic")
public class KpiApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpiApiApplication.class, args);
	}

}
