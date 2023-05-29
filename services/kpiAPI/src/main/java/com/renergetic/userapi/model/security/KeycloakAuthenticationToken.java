package com.renergetic.userapi.model.security;

import com.renergetic.userapi.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class KeycloakAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -3324031070506421715L;
	
	private KeycloakUser principal;
	private Collection<KeycloakRole> roles;
    private Collection<GrantedAuthority> authorities;

    public KeycloakAuthenticationToken(KeycloakUser keycloakUser, List<KeycloakRole> keycloakRoles) {
        super(keycloakRoles);
        this.principal = keycloakUser;
        this.roles = keycloakRoles;
        this.authorities = keycloakRoles.stream().map(role ->  new SimpleGrantedAuthority("ROLE_" + role.name)).collect(Collectors.toList());
    }

    public KeycloakAuthenticationToken(KeycloakUser keycloakUser, List<KeycloakRole> keycloakRoles, User user) {
        super(keycloakRoles);
        this.principal = keycloakUser;
        this.roles = keycloakRoles;
        this.authorities = keycloakRoles.stream().map(role ->  new SimpleGrantedAuthority("ROLE_" + role.name)).collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Collection<KeycloakRole> getRoles() {
        return this.roles;
    }

    @Override
    public KeycloakUser getPrincipal() {
        return principal;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean hasRole(int expectedMask) {
        Optional<Integer> current =
                this.getRoles().stream().map(it -> it.mask).reduce((i, j) -> i | j);
        return (current.get() & expectedMask) > 0;
    }
}
