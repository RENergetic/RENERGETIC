package com.renergetic.hdrapi.model.security;

import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;


public class KeycloakAuthenticationToken extends AbstractAuthenticationToken {
    private KeycloakUser principal;
//    private  List<KeycloakRole> keycloakRoles;

    public KeycloakAuthenticationToken(KeycloakUser user, List<KeycloakRole> keycloakRoles ) {
        super(keycloakRoles);
        this.principal=user;
//        this.keycloakRoles=keycloakRoles;


    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
