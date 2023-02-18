package com.renergetic.hdrapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "PostgreSQL API", version = "1.0", description = "Manage GET, POST, PUT and DELETE request to get or add data from PostgreSQL database"))
public class HdrapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdrapiApplication.class, args);
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//
//                registry.addMapping("/**").allowedOriginPatterns("*")
//                        .allowedOrigins("http://localhost:8080", "https://localhost:8080",
//                        "http://localhost", "https://localhost","http://localhost:3000","https://localhost:3000",
//                        "http://localhost:8082").allowCredentials(true)
//                        .allowedMethods("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("Authorization", "content-type", "language", "Set-Cookie", "X-CSRF-TOKEN",
//                                "Access-Control-Allow-Origin", "Origin", "Content-Type", "Accept")
//                        .exposedHeaders("Authorization", "Set-Cookie", "Content-Disposition", "Location", "CSRF-TOKEN",
//                                "Access-Control-Allow-Origin", "Origin", "Content-Type", "Accept")
//                ;
//                ;
//            }
//        };
//    }
}
