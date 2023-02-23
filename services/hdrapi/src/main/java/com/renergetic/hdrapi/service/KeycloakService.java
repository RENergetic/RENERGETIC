package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.model.security.KeycloakAuthenticationToken;
import com.renergetic.hdrapi.model.security.KeycloakRole;
import com.renergetic.hdrapi.model.security.KeycloakUser;
import com.renergetic.hdrapi.repository.UserRepository;
import com.renergetic.hdrapi.service.utils.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeycloakService {
    @Value("${keycloak.url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value(value = "${keycloak.client-id}")
    private String clientId;


    @Value("${keycloak.admin.username}")
    private String adminUsername;
    @Value("${keycloak.admin.password}")
    private String adminPassword;
    @Value(value = "${keycloak.admin.client-id}")
    private String adminClient;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoggedInService loggedInService;


//    public Keycloak getInstance(String token) {
//        return KeycloakBuilder.builder()
//                .serverUrl(this.serverUrl)
//                .realm(this.realm)
//                .username("admin_test")
//                .password("admin_test")
//                .clientId("admin-cli")
//                .build();
//    }

    public Keycloak getAdminInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(this.serverUrl)
                .realm(this.realm)
                .username(adminUsername)
                .password(adminPassword)
                .clientId("admin-cli")
                .build();
    }

    public Keycloak getAdminInstance(String authToken) {//TODO: verify admin-cli
        return Keycloak.getInstance(serverUrl, realm, "admin-cli", authToken);
    }


    public Keycloak getInstance(String authToken) {
        var clientId = "admin-cli";
        return Keycloak.getInstance(serverUrl, realm, clientId, authToken);
    }

    public KeycloakWrapper getClient(String authToken, boolean admin) {
        if (admin) {
            //TODO: verify if there is no better solution then to just use separate account for the backend
            return new KeycloakWrapper(this.realm, clientId, this.getAdminInstance());
//            return new KeycloakWrapper(this.realm, clientId, this.getAdminInstance(authToken));
        }
        return new KeycloakWrapper(this.realm, clientId, this.getInstance(authToken));
    }

    public KeycloakWrapper getClient(boolean admin) {
        String token = loggedInService.getKeycloakUser().getToken();
        return this.getClient(token, admin);
    }


    public RealmResource getRealmApi(String authToken) {
        Keycloak instance = this.getInstance(authToken);
        return instance.realms().realm(this.realm);
    }

    public ClientResource getClientApi(String authToken) {
        Keycloak instance = this.getInstance(authToken);
        return instance.realms().realm(this.realm).clients().get(this.clientId);
    }

    public KeycloakAuthenticationToken getAuthenticationToken(String keycloakJWTToken) {
        //TODO: verify token and expiration timeout
        try {
            // Split JWT Token
            String[] split_string = keycloakJWTToken.split("\\.");
            //String base64EncodedHeader = split_string[0];
            String base64EncodedBody = split_string[1];
            //String base64EncodedSignature = split_string[2];
            // Decode JWT Token slices
            Base64 base64Url = new Base64(true);
            //String header = new String(base64Url.decode(base64EncodedHeader));
            String body = new String(base64Url.decode(base64EncodedBody));
            // Get the necessary data from the Token body
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
            KeycloakUser user = new KeycloakUser(userId, username, client, keycloakJWTToken);
            return new KeycloakAuthenticationToken(user, keycloakRoles, userRepository);

        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        return null;
    }
}
