package com.renergetic.hdrapi;

import com.renergetic.hdrapi.service.KeycloakService;
import com.renergetic.hdrapi.service.UserService;
import com.renergetic.hdrapi.service.security.KeycloakAuthenticationToken;
import com.renergetic.hdrapi.service.utils.Json;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


//    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    KeycloakService keycloakService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwtToken = request.getHeader("Authorization");
            if (StringUtils.hasText(jwtToken)) {
                KeycloakAuthenticationToken authenticationToken = keycloakService.getAuthenticationToken(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

}
