package com.renergetic.baseapi.config;

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
        
        Map<String, String[]> getUrls = new HashMap<>();
        Map<String, String[]> postUrls = new HashMap<>();
        Map<String, String[]> putUrls = new HashMap<>();
        Map<String, String[]> deleteUrls = new HashMap<>();

        String alertThresholdUrl = "/api/threshold/**";
        String areaUrl = "/api/area/**";
        String assetCategoryUrl = "/api/assetCategories/**";
        String assetUrl = "/api/assets/**";
        String dashboardUrl = "/api/dashboard/**";
        String demandRequestUrl = "/api/demandRequests/**";
        String informationPanelUrl = "/api/informationPanel/**";
        String informationTileUrl = "/api/informationTile/**";
        String logUrl = "/api/log/**";
        String measurementUrl = "/api/measurements/**";
        String notificationUrl = "/api/notification/**";
        String ruleController = "/api/rules/**";

        String[] alertThresholdRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name
        };

        String[] areaRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        String[] assetCategoryRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        String[] assetRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        String[] dashboardRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name
        };

        String[] demandRequestRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name
        };

        String[] getInformationPanelRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name,
            KeycloakRole.REN_USER.name,
            KeycloakRole.REN_VISITOR.name,
            KeycloakRole.REN_STAFF.name
        };

        String[] informationPanelRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        String[] getInformationTileRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name,
            KeycloakRole.REN_USER.name,
            KeycloakRole.REN_VISITOR.name,
            KeycloakRole.REN_STAFF.name
        };

        String[] informationTileRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name
        };

        String[] logRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name,
            KeycloakRole.REN_MANAGER.name
        };

        String[] measurementRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        String[] notificationRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        String[] ruleRoles = new String[] {
            KeycloakRole.REN_DEV.name,
            KeycloakRole.REN_ADMIN.name,
            KeycloakRole.REN_TECHNICAL_MANAGER.name
        };

        getUrls.put(alertThresholdUrl, alertThresholdRoles);
        getUrls.put(areaUrl, areaRoles);
        getUrls.put(assetCategoryUrl, assetCategoryRoles);
        getUrls.put(assetUrl, assetRoles);
        getUrls.put(dashboardUrl, dashboardRoles);
        getUrls.put(demandRequestUrl, demandRequestRoles);
        getUrls.put(informationPanelUrl, getInformationPanelRoles);
        getUrls.put(informationTileUrl, getInformationTileRoles);
        getUrls.put(logUrl, logRoles);
        getUrls.put(measurementUrl, measurementRoles);
        getUrls.put(notificationUrl, notificationRoles);
        getUrls.put(ruleController, ruleRoles);

        postUrls.put(alertThresholdUrl, alertThresholdRoles);
        postUrls.put(areaUrl, areaRoles);
        postUrls.put(assetCategoryUrl, assetCategoryRoles);
        postUrls.put(assetUrl, assetRoles);
        postUrls.put(dashboardUrl, dashboardRoles);
        postUrls.put(demandRequestUrl, demandRequestRoles);
        postUrls.put(informationPanelUrl, informationPanelRoles);
        postUrls.put(informationTileUrl, informationTileRoles);
        postUrls.put(logUrl, logRoles);
        postUrls.put(measurementUrl, measurementRoles);
        postUrls.put(notificationUrl, notificationRoles);
        postUrls.put(ruleController, ruleRoles);

        putUrls.put(alertThresholdUrl, alertThresholdRoles);
        putUrls.put(areaUrl, areaRoles);
        putUrls.put(assetCategoryUrl, assetCategoryRoles);
        putUrls.put(assetUrl, assetRoles);
        putUrls.put(dashboardUrl, dashboardRoles);
        putUrls.put(demandRequestUrl, demandRequestRoles);
        putUrls.put(informationPanelUrl, informationPanelRoles);
        putUrls.put(informationTileUrl, informationTileRoles);
        putUrls.put(logUrl, logRoles);
        putUrls.put(measurementUrl, measurementRoles);
        putUrls.put(notificationUrl, notificationRoles);
        putUrls.put(ruleController, ruleRoles);

        deleteUrls.put(alertThresholdUrl, alertThresholdRoles);
        deleteUrls.put(areaUrl, areaRoles);
        deleteUrls.put(assetCategoryUrl, assetCategoryRoles);
        deleteUrls.put(assetUrl, assetRoles);
        deleteUrls.put(dashboardUrl, dashboardRoles);
        deleteUrls.put(demandRequestUrl, demandRequestRoles);
        deleteUrls.put(informationPanelUrl, informationPanelRoles);
        deleteUrls.put(informationTileUrl, informationTileRoles);
        deleteUrls.put(logUrl, logRoles);
        deleteUrls.put(measurementUrl, measurementRoles);
        deleteUrls.put(notificationUrl, notificationRoles);
        deleteUrls.put(ruleController, ruleRoles);

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
        //registry.anyRequest().permitAll();

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

}