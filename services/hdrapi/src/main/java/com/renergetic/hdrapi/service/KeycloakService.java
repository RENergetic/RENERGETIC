package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.service.security.KeycloakAuthenticationToken;
import com.renergetic.hdrapi.service.security.KeycloakRole;
import com.renergetic.hdrapi.service.security.KeycloakUser;
import com.renergetic.hdrapi.service.utils.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeycloakService {
    //    @Value("${keycloak.url:http://10.0.0.9:8080/auth}")
    @Value("${keycloak.url:http://localhost:3080/auth}")
    String serverUrl;
    @Value("${keycloak.realm:master}")
    String realm;
    @Value(value = "${keycloak.client-id:vue-test}")
    String clientId;

    public Keycloak getInstance(String username, String password) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .build();
    }

    public Keycloak getInstance(String authToken) {
        return Keycloak.getInstance(serverUrl, realm, clientId, authToken);
    }


    public KeycloakAuthenticationToken getAuthenticationToken(String keycloakJWTToken) {
        try {
            String[] split_string = keycloakJWTToken.split("\\.");
            String base64EncodedHeader = split_string[0];
            String base64EncodedBody = split_string[1];
            String base64EncodedSignature = split_string[2];
            Base64 base64Url = new Base64(true);
            String header = new String(base64Url.decode(base64EncodedHeader));
            String body = new String(base64Url.decode(base64EncodedBody));
            JSONObject keycloakJSON = Json.parse(body);
            var userId = keycloakJSON.get("sub").toString();
            var client = keycloakJSON.get("azp").toString();
            if (!client.equals(clientId)) {
                throw new Exception("Invalid client" + clientId);
            }
            var username = keycloakJSON.get("preferred_username").toString();
            var roles = keycloakJSON.getJSONObject("resource_access").getJSONObject(client).getJSONArray("roles");
            List<KeycloakRole> keycloakRoles =
                    roles.toList().stream().map(it -> KeycloakRole.roleByName(it.toString())).collect(
                            Collectors.toList());
            KeycloakUser user = new KeycloakUser(userId, username, client);
            return new KeycloakAuthenticationToken(user, keycloakRoles);

        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        return null;
    }
}
