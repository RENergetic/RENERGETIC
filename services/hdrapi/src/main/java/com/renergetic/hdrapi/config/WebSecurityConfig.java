package com.renergetic.hdrapi.config;

import com.renergetic.common.model.security.KeycloakAuthenticationToken;
import com.renergetic.common.model.security.KeycloakRole;
import com.renergetic.hdrapi.service.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
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
public class WebSecurityConfig {

    @Value("#{${api.cors.allowed-origins}}")
    List<String> origins;
    @Value("#{${api.cors.allowed-methods}}")
    List<String> methods;
    @Value("${api.cors.max-age}")
    Long maxAge;

    @Value(value = "${keycloak.client-id}")
    private String clientId;

    @Autowired
    JwtAuthenticationProvider authProvider;

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
//        #TODO
//    ### GET METHODS ###
        getUrls.put("/api/users/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name});
        getUrls.put("/api/users/**",
                new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                        KeycloakRole.REN_VISITOR.name, KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                        KeycloakRole.REN_TECHNICAL_MANAGER.name});
        getUrls.put("/api/users/profile",new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        getUrls.put("/api/users/profile/settings",new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        getUrls.put("/api/users/notifications",
                new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name});
        getUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        getUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
//    ### POST METHODS ###
        postUrls.put("/api/users/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        postUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        postUrls.put("/api/users/profile/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
//    ### PUT METHODS ###
        putUrls.put("/api/users/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        putUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        putUrls.put("/api/users/profile/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
//    ### DELETE METHODS ###
        deleteUrls.put("/api/users/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});
        deleteUrls.put("/api/dashboard/**", new String[]{KeycloakRole.REN_USER.name, KeycloakRole.REN_STAFF.name, KeycloakRole.REN_MANAGER.name,
                KeycloakRole.REN_VISITOR.name,KeycloakRole.REN_DEV.name, KeycloakRole.REN_ADMIN.name,
                KeycloakRole.REN_TECHNICAL_MANAGER.name, KeycloakRole.REN_MANAGER.name});

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
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
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

    }
//
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }


}