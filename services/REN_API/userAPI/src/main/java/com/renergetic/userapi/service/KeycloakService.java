package com.renergetic.userapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.userapi.dao.UserDAORequest;
import com.renergetic.userapi.model.security.KeycloakRole;

@Service
public class KeycloakService {

    @Value("${keycloak.url}")
    private String url;
    @Value("${keycloak.realm}")
    private String realm;
    @Value(value = "${keycloak.client-id}")
    private String client;

    @Value("${keycloak.admin.username}")
    private String adminUsername;
    @Value("${keycloak.admin.password}")
    private String adminPassword;
    @Value(value = "${keycloak.admin.client-id}")
    private String adminClient;
    
    private String clientId;
    
    private RealmResource realmApi;
    
    private String getClientId() {
    	if (this.clientId == null) {
    		this.clientId = this.getRealmApi()
                .clients()
                .findAll()
                .stream()
                .filter(it -> it.getClientId().equals(client))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("client not found " + client))
                .getId();
    	}
        return this.clientId;
    }

    private RealmResource getRealmApi() {
    	if (this.realmApi == null) {
    		this.realmApi = KeycloakBuilder.builder()
	            .serverUrl(url)
	            .realm(realm)
	            .username(adminUsername)
	            .password(adminPassword)
	            .clientId(adminClient)
	            .build()
	            .realm(realm);
    	}
        return this.realmApi;
    }

    public UserResource getUser(String id) {
        return getRealmApi().users().get(id);
    }

    public UserRepresentation updateUser(UserDAORequest user) {
        UserResource userResource = getRealmApi().users().get(user.getId());
        UserRepresentation current = userResource.toRepresentation();
        current = user.update(current);
        userResource.update(current);
        return current;
    }

    public UserRepresentation deleteUser(String id) {
        UserResource userResource = getRealmApi().users().get(id);
        UserRepresentation current = userResource.toRepresentation();
        getRealmApi().users().delete(id);
        return current;
    }

    public UserRepresentation createUser(UserDAORequest user) {
        UserRepresentation userRepresentation = user.mapToKeycloakEntity();
        userRepresentation.setEnabled(true);

        getRealmApi().users().create(userRepresentation);
        String username = userRepresentation.getUsername();
        Optional<UserRepresentation> first =
                this.getRealmApi().users().search(userRepresentation.getUsername()).stream().filter(
                        it -> it.getUsername().equals(username)).findFirst();
        if (first.isEmpty()) {
            throw new NotFoundException("user not saved " + user.getUsername());
        }

        userRepresentation = first.get();
        if (user.getPassword() != null) {
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType("password");
            credentialRepresentation.setValue(user.getPassword());
            getRealmApi().users().get(userRepresentation.getId()).resetPassword(credentialRepresentation);
        }
        this.assignRole(userRepresentation.getId(), KeycloakRole.REN_GUEST.name);

        return userRepresentation;
    }
    public void updatePassword(String userId,String password){

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType("password");
            credentialRepresentation.setValue(password);
            getRealmApi().users().get(userId).resetPassword(credentialRepresentation);

    }

    public List<UserRepresentation> listUsers(int offset,int limit) {
        return getRealmApi().users().list(  offset,  limit);
    }

    public List<UserRepresentation> listUsers(String role,int offset,int limit) {
        return new ArrayList<UserRepresentation>(getRealmApi().roles().get(role).getRoleUserMembers(offset,limit));
    }

    public RoleRepresentation getRole(String role) {
        return this.getRealmApi().clients().get(this.getClientId()).roles().get(role).toRepresentation();
    }

    public List<RoleRepresentation> assignRole(String userId, String roleName) {
        RoleRepresentation role = this.getRole(roleName);
        UserResource user = this.getUser(userId);

        RoleScopeResource roleScopeResource = user.roles().clientLevel(this.getClientId());
        roleScopeResource.add(List.of(role));
        return roleScopeResource.listEffective();
    }

    public List<RoleRepresentation> revokeRole(String userId, String roleName) {
        RoleRepresentation role = this.getRole(roleName);
        UserResource user = this.getUser(userId);
        RoleScopeResource roleScopeResource = user.roles().clientLevel(this.getClientId());
        roleScopeResource.remove(List.of(role));
        return roleScopeResource.listEffective();
    }


    public List<RoleRepresentation> getRoles(String userId) {
        UserResource user = this.getUser(userId);
        try {
            RoleScopeResource roleScopeResource = user.roles().clientLevel(this.getClientId());
            return roleScopeResource.listEffective();
        } catch (NullPointerException ex) {
            return Collections.emptyList();
        }
    }

}
