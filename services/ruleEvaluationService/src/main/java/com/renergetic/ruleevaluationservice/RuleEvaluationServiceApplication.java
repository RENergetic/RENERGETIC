package com.renergetic.ruleevaluationservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.renergetic")
@EnableJpaRepositories(basePackages = "com.renergetic")
@EntityScan(basePackages = "com.renergetic")
@ComponentScan(basePackages = "com.renergetic")
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "PostgreSQL API", version = "1.0", description = "Manage and schedule rule evaluation"))
public class RuleEvaluationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleEvaluationServiceApplication.class, args);
    }
}
