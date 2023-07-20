package com.renergetic.ruleevaluationservice.config;

import com.renergetic.common.model.security.KeycloakAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

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
