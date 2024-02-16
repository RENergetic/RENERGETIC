package com.renergetic.kubeflowapi.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.renergetic.common.config.JwtAuthenticationFilter;
import com.renergetic.common.config.JwtAuthorizationManager;
import com.renergetic.common.model.security.KeycloakRole;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("#{${api.cors.allowed-origins}}")
    List<String> origins;
    @Value("#{${api.cors.allowed-methods}}")
    List<String> methods;
    @Value("${api.cors.max-age}")
    Long maxAge;

    @Value(value = "${keycloak.client-id}")
    private String clientId;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setClientId(clientId);

        http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, AuthorizationFilter.class);
        
        Map<String, KeycloakRole[]> getUrls = new HashMap<>();
        Map<String, KeycloakRole[]> postUrls = new HashMap<>();
        Map<String, KeycloakRole[]> putUrls = new HashMap<>();
        Map<String, KeycloakRole[]> deleteUrls = new HashMap<>();
        
        
        // getUrls.put("/api/example/**", new KeycloakRole[]{KeycloakRole.REN_DEV, KeycloakRole.REN_ADMIN});
        
        // postUrls.put("/api/example/**", new KeycloakRole[]{KeycloakRole.REN_DEV, KeycloakRole.REN_ADMIN});
        
        // putUrls.put("/api/example/**", new KeycloakRole[]{KeycloakRole.REN_DEV, KeycloakRole.REN_ADMIN});
        
        // deleteUrls.put("/api/example/**", new KeycloakRole[]{KeycloakRole.REN_DEV, KeycloakRole.REN_ADMIN});

        http.authorizeHttpRequests(registry -> {
	        
	        getUrls.forEach((urlPattern, roles) -> {
	        	registry.requestMatchers(HttpMethod.GET, urlPattern).access(new JwtAuthorizationManager(roles));
	        });
	        
	        postUrls.forEach((urlPattern, roles) -> {
	        	registry.requestMatchers(HttpMethod.POST, urlPattern).access(new JwtAuthorizationManager(roles));
	        });
	        
	        putUrls.forEach((urlPattern, roles) -> {
	        	registry.requestMatchers(HttpMethod.PUT, urlPattern).access(new JwtAuthorizationManager(roles));
	        });
	        
	        deleteUrls.forEach((urlPattern, roles) -> {
	        	registry.requestMatchers(HttpMethod.DELETE, urlPattern).access(new JwtAuthorizationManager(roles));
	        });
	        
	    //     registry.anyRequest().access(new JwtAuthorizationManager()); // Access to authenticated users 
                registry.anyRequest().permitAll(); // Access to all users
        });
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    	
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(methods);
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "content-type", "language", "Set-Cookie", "X-CSRF-TOKEN"));
        configuration.setExposedHeaders(
                Arrays.asList("Authorization", "Set-Cookie", "Content-Disposition", "Location", "CSRF-TOKEN"));

        configuration.setMaxAge(maxAge);
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}