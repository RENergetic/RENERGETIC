package com.renergetic.hdrapi.model.security;

import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.User;
import com.renergetic.hdrapi.repository.UserRepository;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class KeycloakAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -3324031070506421715L;
	
	private KeycloakUser principal;
    private User user;
    private UserRepository userRepository;
//    private List<KeycloakRole> keycloakRoles;

    public KeycloakAuthenticationToken(KeycloakUser keycloakUser, List<KeycloakRole> keycloakRoles,
                                       UserRepository userRepository) {
        super(keycloakRoles);
        this.principal = keycloakUser;
        this.userRepository = userRepository;
//        this.keycloakRoles = keycloakRoles;
    }

    public KeycloakAuthenticationToken(KeycloakUser keycloakUser, List<KeycloakRole> keycloakRoles, User user) {
        super(keycloakRoles);
        this.principal = keycloakUser;
        this.user = user;
//        this.keycloakRoles = keycloakRoles;
    }

    public User getUser() {
        if (user != null) {
            return user;
        }
        if (this.userRepository != null) {
            this.user = this.userRepository.findByKeycloakId(this.principal.getId());
            if (user == null) {
                throw new NotFoundException("No matching renergetic user for: " + this.principal.getId());
            }
        }
        return user;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Collection<KeycloakRole> getRoles() {
        return this.getAuthorities().stream().map(it -> (KeycloakRole) it).collect(Collectors.toList());
    }

    @Override
    public KeycloakUser getPrincipal() {
        return principal;
    }

    public boolean hasRole(int expectedMask) {
        Optional<Integer> current =
                this.getRoles().stream().map(it -> it.mask).reduce((i, j) -> i | j);
        return (current.get() & expectedMask) > 0;
    }
}
