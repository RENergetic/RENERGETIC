package com.renergetic.api;

import com.renergetic.api.service.InitialConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.renergetic")
@EntityScan(basePackages = "com.renergetic")
@SpringBootApplication(scanBasePackages = "com.renergetic")
@ComponentScan(basePackages = "com.renergetic")
public class ApiApplication {
	@Autowired
	InitialConfigurationService initialConfigurationService;

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void configureEnvironment() {
		System.out.println("Ready to run");
		initialConfigurationService.configure();
	}

}
