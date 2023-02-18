package com.renergetic.hdrapi;

import com.renergetic.hdrapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
        //Superadmin access
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET).permitAll()
                .antMatchers(HttpMethod.PUT).permitAll()
                .antMatchers(HttpMethod.POST).permitAll()
                .antMatchers(HttpMethod.DELETE).permitAll() ;
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    	origins.stream().forEach(System.err::println);
    	methods.stream().forEach(System.err::println);
    	System.err.println(maxAge);
    	
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

//
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }



//
//    @Bean
//    public CustomUserDetailsService userDetailsService() {
//        return new CustomUserDetailsService(userRepository);
//    }


}