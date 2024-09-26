package com.renergetic.userapi.config;

import com.renergetic.common.model.security.KeycloakAuthenticationToken;
import com.renergetic.common.model.security.KeycloakRole;
import com.renergetic.common.config.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    protected JwtAuthenticationProvider authenticationProvider() throws Exception {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider();
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setClientId(clientId);

        http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, FilterSecurityInterceptor.class);
        
        final String profileUrl = "/api/user/profile/**";        
        final String assetUrl = "/api/user/assets/**";
        final String notificationsUrl = "/api/user/notifications/**";
        final String adminUrl = "/api/manage/user/**";

        final String[] profileRoles = new String[]{
            KeycloakRole.REN_DEV.name, 
            KeycloakRole.REN_ADMIN.name, 
            KeycloakRole.REN_TECHNICAL_MANAGER.name, 
            KeycloakRole.REN_MANAGER.name, 
            KeycloakRole.REN_USER.name,
            KeycloakRole.REN_VISITOR.name,
            KeycloakRole.REN_GUEST.name,
            KeycloakRole.REN_STAFF.name};

        final String[] assetRoles = new String[]{
            KeycloakRole.REN_DEV.name, 
            KeycloakRole.REN_ADMIN.name, 
            KeycloakRole.REN_TECHNICAL_MANAGER.name, 
            KeycloakRole.REN_MANAGER.name, 
            KeycloakRole.REN_USER.name,
            KeycloakRole.REN_VISITOR.name,
            KeycloakRole.REN_GUEST.name,
            KeycloakRole.REN_STAFF.name};

        final String[] notificationsRoles = new String[]{
            KeycloakRole.REN_DEV.name, 
            KeycloakRole.REN_ADMIN.name, 
            KeycloakRole.REN_TECHNICAL_MANAGER.name, 
            KeycloakRole.REN_MANAGER.name, 
            KeycloakRole.REN_USER.name,
            KeycloakRole.REN_STAFF.name};

        final String[] adminRoles = new String[]{
            KeycloakRole.REN_DEV.name, 
            KeycloakRole.REN_ADMIN.name, 
            KeycloakRole.REN_TECHNICAL_MANAGER.name};

        Map<String, String[]> getUrls = new HashMap<>();
        Map<String, String[]> postUrls = new HashMap<>();
        Map<String, String[]> putUrls = new HashMap<>();
        Map<String, String[]> deleteUrls = new HashMap<>();

        configureRoles(profileUrl, profileRoles, getUrls, postUrls, putUrls, deleteUrls);
        configureRoles(assetUrl, assetRoles, getUrls, postUrls, putUrls, deleteUrls);
        configureRoles(notificationsUrl, notificationsRoles, getUrls, postUrls, putUrls, deleteUrls);
        configureRoles(adminUrl, adminRoles, getUrls, postUrls, putUrls, deleteUrls);

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                .ExpressionInterceptUrlRegistry registry = http.csrf().disable().authorizeRequests();

        getUrls.forEach((urlPattern, roles) -> {
            registry.antMatchers(HttpMethod.GET, urlPattern).hasAnyRole(roles);
        });

        postUrls.forEach((urlPattern, roles) -> {
            registry.antMatchers(HttpMethod.POST, urlPattern).hasAnyRole(roles);
        });

        putUrls.forEach((urlPattern, roles) -> {
            registry.antMatchers(HttpMethod.PUT, urlPattern).hasAnyRole(roles);
        });

        deleteUrls.forEach((urlPattern, roles) -> {
            registry.antMatchers(HttpMethod.DELETE, urlPattern).hasAnyRole(roles);
        });
        registry.anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // registry.anyRequest().permitAll();

        return http.build();
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
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

    private void configureRoles(
        String url,
        String[] roles,
        Map<String, String[]> getUrls, 
        Map<String, String[]> postUrls, 
        Map<String, String[]> putUrls, 
        Map<String, String[]> deleteUrls) {
        getUrls.put(url, roles);
        postUrls.put(url, roles);
        putUrls.put(url, roles);
        deleteUrls.put(url, roles);
    }
}