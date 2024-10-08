package com.renergetic.kpiapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.exception.UnauthorizedAccessException;
import com.renergetic.kpiapi.model.User;
import com.renergetic.kpiapi.model.security.KeycloakAuthenticationToken;
import com.renergetic.kpiapi.repository.UserRepository;

public class LoggedInService {
	
	@Autowired
	UserRepository userRepo;
	
	public KeycloakAuthenticationToken getAuthenticationData() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof KeycloakAuthenticationToken) {
			return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		}
		
		if (auth == null || auth instanceof AnonymousAuthenticationToken)
			throw new UnauthorizedAccessException("Empty Authoritation header");
		else
			throw new UnauthorizedAccessException("Invalid credentials");
	}
	
	public User getUser() {
        KeycloakAuthenticationToken auth = getAuthenticationData();

        // If user doesn't exist it must send a not found exception
        User user = this.userRepo.findByKeycloakId(auth.getPrincipal().getId())
        		.orElseThrow(() -> new NotFoundException("No matching renergetic user for: " + auth.getPrincipal().getId()));
        return user;
	}
}
