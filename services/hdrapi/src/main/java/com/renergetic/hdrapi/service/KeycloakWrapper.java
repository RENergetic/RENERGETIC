package com.renergetic.hdrapi.service;

import lombok.AccessLevel;
import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;


@Getter
public class KeycloakWrapper {
    private String realm;
    private String clientId;
    private Keycloak keycloak;
    @Getter(AccessLevel.NONE)
    private RealmResource realmApi;

    public KeycloakWrapper(String realm, String clientId, Keycloak keycloak) {
        this.realm = realm;
        this.clientId = clientId;
        this.keycloak = keycloak;
    }

    synchronized private RealmResource getRealmApi() {
        if (this.realmApi == null) {
            this.realmApi = keycloak.realm(realm);
        }
        return this.realmApi;
    }

    public UserResource getUser(String id) {

        return getRealmApi().users().get(id);
    }

    public RoleRepresentation getRole(String role) {
        return this.getRealmApi().clients().get(this.clientId).roles().get(role).toRepresentation();
    }

    public List<RoleRepresentation> assignRole(String userId, String roleName) {
        RoleRepresentation role = this.getRole(roleName);
        UserResource user = this.getUser(userId);

        RoleScopeResource roleScopeResource = user.roles().clientLevel(this.clientId);
        roleScopeResource.add(List.of(role));
        return roleScopeResource.listAll();
    }

    public List<RoleRepresentation> revokeRole(String userId, String roleName) {
        RoleRepresentation role = this.getRole(roleName);
        UserResource user = this.getUser(userId);
        RoleScopeResource roleScopeResource = user.roles().clientLevel(this.clientId);
        roleScopeResource.remove(List.of(role));
        return roleScopeResource.listAll();
    }
}
