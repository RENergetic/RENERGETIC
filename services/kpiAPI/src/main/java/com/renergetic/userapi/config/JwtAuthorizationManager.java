package com.renergetic.userapi.config;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import com.renergetic.userapi.model.security.KeycloakAuthenticationToken;
import com.renergetic.userapi.model.security.KeycloakRole;

@Component
public class JwtAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
	
	private List<KeycloakRole> requiredRoles;
	
	public JwtAuthorizationManager() {
		requiredRoles = List.of();
	}
	
	public JwtAuthorizationManager(KeycloakRole...roles) {
		requiredRoles = Arrays.asList(roles);
	}
	
	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		if (authentication.get() instanceof KeycloakAuthenticationToken) {
			KeycloakAuthenticationToken keycloakAuth = (KeycloakAuthenticationToken) authentication.get();
			
			if (requiredRoles.isEmpty() || requiredRoles.stream().anyMatch(role -> keycloakAuth.getRoles().contains(role)))
				return new AuthorizationDecision(true);
		}
		return new AuthorizationDecision(false);
	}

}
