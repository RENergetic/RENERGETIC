package com.renergetic.hdrapi.config;

import com.renergetic.hdrapi.model.security.KeycloakAuthenticationToken;
import com.renergetic.hdrapi.model.security.KeycloakRole;
import com.renergetic.hdrapi.service.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static pl.psnc.vtl.portal.model.users.SecurityGroup.SUPER_ADMIN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("#{${api.cors.allowed-origins}}")
    List<String> origins;
    @Value("#{${api.cors.allowed-methods}}")
    List<String> methods;
    @Value("${api.cors.max-age}")
    Long maxAge;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable();
        //for some reason it doesnt work
//        http.csrf().disable().authorizeRequests().antMatchers("/api/users")
//                .hasAnyRole(KeycloakRole.REN_ADMIN.getAuthority(),
//                        KeycloakRole.REN_TECHNICAL_MANAGER.getAuthority())
//                .and().exceptionHandling().accessDeniedHandler(
//                accessDeniedHandler()
//        );
//        http.csrf().disable().antMatcher("/api/users").addFilterAfter(
//                new RoleFilter(KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask),
//                JwtAuthenticationFilter.class);
        Map<String, String[]> getUrls = new HashMap<>();
        Map<String, String[]> postUrls = new HashMap<>();
        Map<String, String[]> putUrls = new HashMap<>();
        Map<String, String[]> deleteUrls = new HashMap<>();
        
        getUrls.put("/api/users/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name});
        getUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        
        postUrls.put("/api/users/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name});
        postUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        
        putUrls.put("/api/users/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name});
        putUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        
        deleteUrls.put("/api/users/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name});
        deleteUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name, KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        
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
        //registry.anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        registry.anyRequest().permitAll();
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    	
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

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    public class RoleFilter implements Filter {
        private final int expectedMask;

        public RoleFilter(int expectedMask) {
            this.expectedMask = expectedMask;
        }

        @Override
        public void destroy() {
        }

        @Override
        public void doFilter(ServletRequest req, ServletResponse res,
                             FilterChain chain) throws IOException, ServletException {


            KeycloakAuthenticationToken authentication =
                    (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (authentication.hasRole(expectedMask)) {
                chain.doFilter(req, res);
            } else {
                System.err.println("TODO:");
                //TODO:
            }

        }

        @Override
        public void init(FilterConfig arg0) throws ServletException {
            // Do nothing
        }

    }
//
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }




}