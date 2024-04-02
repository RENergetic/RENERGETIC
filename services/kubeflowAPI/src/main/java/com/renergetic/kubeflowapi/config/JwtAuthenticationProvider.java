package com.renergetic.kubeflowapi.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.renergetic.common.model.security.KeycloakAuthenticationToken;

public class JwtAuthenticationProvider implements AuthenticationProvider {
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authentication;
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(KeycloakAuthenticationToken.class);
	}

}
