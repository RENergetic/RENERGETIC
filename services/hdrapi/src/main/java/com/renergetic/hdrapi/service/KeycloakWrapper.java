package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.UserDAORequest;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.security.KeycloakRole;
import lombok.AccessLevel;
import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//TODO: handle errors
@Getter
public class KeycloakWrapper {
    private String realm;
    private String clientId;
    private Keycloak keycloak;
    @Getter(AccessLevel.NONE)
    private RealmResource realmApi;
    @Getter(AccessLevel.NONE)
    //id field
    private String clientKeycloakId;

    public KeycloakWrapper(String realm, String clientId, Keycloak keycloak) {
        this.realm = realm;
        this.clientId = clientId;
        this.keycloak = keycloak;
    }

    private String getClient_Id() {
        if (clientKeycloakId != null) {
            return clientKeycloakId;
        }
        Optional<ClientRepresentation> first =
                this.getRealmApi().clients().findAll().stream().filter(
                        it -> it.getClientId().equals(clientId)).findFirst();
        if (!first.isPresent()) {
            throw new NotFoundException("client not found " + clientId);
        }
        this.clientKeycloakId = first.get().getId();
        return this.clientKeycloakId;
    }

    private RealmResource getRealmApi() {
        if (this.realmApi == null) {
            this.realmApi = keycloak.realm(realm);
        }
        return this.realmApi;
    }

    public UserResource getUser(String id) {

        return getRealmApi().users().get(id);
    }
    public boolean updateUser( UserDAORequest user) {
        UserResource userResource = getRealmApi().users().get(user.getId());
        UserRepresentation current = userResource.toRepresentation();
        current= user.update(current);
        userResource.update(current);
        return true;
    }

    public List<UserRepresentation> listUsers() {
        return getRealmApi().users().list();
        //TODO: make every user with guest role
//        return new ArrayList<>(this.getRealmApi().clients().get(this.getClient_Id()).roles().get(
//                KeycloakRole.REN_GUEST.name).getRoleUserMembers());
    }

    public RoleRepresentation getRole(String role) {
        return this.getRealmApi().clients().get(this.getClient_Id()).roles().get(role).toRepresentation();
    }

    public List<RoleRepresentation> assignRole(String userId, String roleName) {
        RoleRepresentation role = this.getRole(roleName);
        UserResource user = this.getUser(userId);

        RoleScopeResource roleScopeResource = user.roles().clientLevel(this.getClient_Id());
        roleScopeResource.add(List.of(role));
        return roleScopeResource.listEffective();
    }

    public List<RoleRepresentation> revokeRole(String userId, String roleName) {
        RoleRepresentation role = this.getRole(roleName);
        UserResource user = this.getUser(userId);
        RoleScopeResource roleScopeResource = user.roles().clientLevel(this.getClient_Id());
        roleScopeResource.remove(List.of(role));
        return roleScopeResource.listEffective();
    }


    public List<RoleRepresentation> getRoles(String userId) {
        UserResource user = this.getUser(userId);
        try {
//            List<RoleRepresentation> roleScopeResource =
//                    user.roles().getAll().getClientMappings().get(this.getClientId()).getMappings();

            RoleScopeResource roleScopeResource = user.roles().clientLevel(this.getClient_Id());
            return roleScopeResource.listEffective();
        } catch (NullPointerException ex) {
            return Collections.emptyList();
        }


    }


}
