package com.renergetic.hdrapi.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakService {
    @Value("${keycloak.url}")
    String serverUrl;
    @Value("${keycloak.realm}")
    String realm;
    @Value("${keycloak.client-id}")
    String clientId;

    public Keycloak getInstance(String username,String password) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .build();
    }

    public Keycloak getInstance(String authToken) {
        return Keycloak.getInstance(serverUrl,realm,clientId,authToken);
    }
}
