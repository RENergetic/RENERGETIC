package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.exception.UnauthorizedAccessException;
import com.renergetic.hdrapi.model.User;
import com.renergetic.hdrapi.model.security.KeycloakAuthenticationToken;
import com.renergetic.hdrapi.model.security.KeycloakRole;
import com.renergetic.hdrapi.model.security.KeycloakUser;
import com.renergetic.hdrapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LoggedInService {

    private final UserRepository userRepository;
    @Autowired
    KeycloakService keycloakService;

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken || authentication == null) {
            throw new UnauthorizedAccessException("Empty Authoritation header");
        } else if (authentication instanceof KeycloakAuthenticationToken) {

            KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) authentication;

            // If user doesn't exist it must send a not found exception
            return keycloakAuthenticationToken.getUser();
        } else {
            //TODO:
            throw new UnauthorizedAccessException("Invalid Authoritation header to get the logged user");
        }
    }

    private KeycloakAuthenticationToken getKeyCloakToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) authentication;
            return keycloakAuthenticationToken;
        } else {
            //TODO:
            throw new UnauthorizedAccessException("Invalid Authoritation header, doesn't contain a token");
        }
    }

    public KeycloakUser getKeycloakUser() {
        return getKeyCloakToken().getPrincipal();
    }

    public KeycloakWrapper getKeycloakClient(boolean admin) {
        String token = this.getKeycloakUser().getToken();
        //verify
        if (admin && !this.hasRole(KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask)) {
            //TODO: raise exception
        }
        return keycloakService.getClient(token, true);
    }

    public boolean hasRole(List<KeycloakRole> roles) {
        Optional<Integer> expected = roles.stream().map(it -> it.mask).reduce((i, j) -> i | j);
        return this.hasRole(expected.get());
    }

    public boolean hasRole(int expectedMask) {
        if (this.getKeyCloakToken() == null) {
            return false;
        }
        return this.getKeyCloakToken().hasRole(expectedMask);
    }


}

