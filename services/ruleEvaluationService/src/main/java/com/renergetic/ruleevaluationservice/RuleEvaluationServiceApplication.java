package com.renergetic.ruleevaluationservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "PostgreSQL API", version = "1.0", description = "Manage GET, POST, PUT and DELETE request to get or add data from PostgreSQL database"))
public class RuleEvaluationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuleEvaluationServiceApplication.class, args);
    }
}
