package com.renergetic.kubeflowapi.config;

import com.renergetic.common.model.security.KeycloakAuthenticationToken;
import com.renergetic.common.model.security.KeycloakRole;
import com.renergetic.common.model.security.KeycloakUser;

import com.renergetic.common.utilities.Json;
import lombok.Getter; 
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private String clientId;


    @Override
    protected void doFilterInternal(   HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwtToken = request.getHeader("Authorization");
            if (StringUtils.hasText(jwtToken)) {
                KeycloakAuthenticationToken authenticationToken = this.getAuthenticationToken(jwtToken);

            	SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    	request.logout();
    }
    
    @Override
	protected boolean shouldNotFilterAsyncDispatch() {
		return false;
	}

    public KeycloakAuthenticationToken getAuthenticationToken(String keycloakJWTToken) {
        //TODO: verify token and expiration timeout
        try {
            // Split JWT Token
            String[] split_string = keycloakJWTToken.split("\\.");
            //String base64EncodedHeader = split_string[0];
            String base64EncodedBody = split_string[1];
            //String base64EncodedSignature = split_string[2];
            // Decode JWT Token slices
            Base64 base64Url = new Base64(true);
            //String header = new String(base64Url.decode(base64EncodedHeader));
            String body = new String(base64Url.decode(base64EncodedBody));
            // Get the necessary data from the Token body
            JSONObject keycloakJSON = Json.parse(body);
            var userId = keycloakJSON.get("sub").toString();
            var client = keycloakJSON.get("azp").toString();
            if (!client.equals(clientId)) {
                throw new Exception("Invalid client" + clientId);
            }
            var username = keycloakJSON.get("preferred_username").toString();
            var roles = keycloakJSON.getJSONObject("resource_access").getJSONObject(client).getJSONArray("roles");
            List<KeycloakRole> keycloakRoles =
                    roles.toList().stream().map(it -> KeycloakRole.roleByName(it.toString())).collect(
                            Collectors.toList());
            KeycloakUser user = new KeycloakUser(userId, username, client, keycloakJWTToken);
            return new KeycloakAuthenticationToken(user, keycloakRoles);

        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        return null;
    }
}
