package com.renergetic.hdrapi.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakService {
    @Value("${keycloak.url:http://10.0.0.9:8080/auth}")
    String serverUrl;
    @Value("${keycloak.realm:master}")
    String realm;
    @Value(value = "${keycloak.client-id:vue-test}")
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
