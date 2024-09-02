package com.renergetic.api.service;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.renergetic.api.model.*;
import com.renergetic.common.model.*;
import com.renergetic.common.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.*;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;

@Service
@Slf4j
public class InitialConfigurationService {
    @Value("${keycloak.url}")
    private String url;
    @Value("${keycloak.default-realm}")
    private String defaultRealm;
    @Value("${keycloak.admin.username}")
    private String adminUsername;
    @Value("${keycloak.admin.password}")
    private String adminPassword;
    @Value("${keycloak.admin.client-id}")
    private String adminClientId;


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    @Autowired
    private UuidRepository uuidRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetTypeRepository assetTypeRepository;

    public void configure() {
        //TODO: Check if configuration already exists
        //TODO: Print, if created, the information about realm (what was changed, created, ..., and always the client id + admin client id)
        Gson gson = new Gson();
        JsonReader reader = null;
        RealmConfiguration realmConfiguration = null;
        try {
            InputStream is = getClass().getResourceAsStream("/keycloak-config.json");
            reader = new JsonReader(new InputStreamReader(is));
            realmConfiguration = gson.fromJson(reader, RealmConfiguration.class);
        } catch(Exception e) {
            log.error("Could not deserialize json configuration.", e);
            System.exit(1);
        }

        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(url)
                    .username(adminUsername)
                    .password(adminPassword)
                    .realm(defaultRealm)
                    .clientId(adminClientId)
                    .build();
        } catch (Exception e) {
            log.error("Could not connect to Keycloak service.", e);
            System.exit(1);
        }

        createRealmIfNotExist(keycloak, realmConfiguration);
        createClientsIfNotExist(keycloak, realmConfiguration);
        createUsersIfNotExist(keycloak, realmConfiguration);

        System.exit(0);
    }

    private void createRealmIfNotExist(Keycloak keycloak, RealmConfiguration realmConfiguration) {
        List<RealmRepresentation> realms = keycloak.realms().findAll();
        Optional<RealmRepresentation> renRealm = realms.stream()
                .filter(x -> x.getRealm().equals(realmConfiguration.getRealm())).findFirst();

        if(renRealm.isEmpty()) {
            log.info("Realm {} not existing, creating it.", realmConfiguration.getRealm());
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(realmConfiguration.getRealm());
            realmRepresentation.setEnabled(true);
            realmRepresentation.setRegistrationAllowed(realmConfiguration.isRegistrationAllowed());
            keycloak.realms().create(realmRepresentation);
            log.info("Realm {} created.", realmConfiguration.getRealm());
        } else {
            log.info("Realm {} already existing.", realmConfiguration.getRealm());
        }
    }

    private void createClientsIfNotExist(Keycloak keycloak, RealmConfiguration realmConfiguration){
        List<ClientRepresentation> clients = keycloak.realm(realmConfiguration.getRealm()).clients().findAll();
        for(ClientConfiguration clientConfiguration : realmConfiguration.getClients()) {
            Optional<ClientRepresentation> client = clients.stream()
                    .filter(x -> x.getClientId().equals(clientConfiguration.getClientId())).findFirst();
            if(client.isEmpty()) {
                log.info("Client {} not existing, creating it.", clientConfiguration.getClientId());
                String id = createClient(keycloak, realmConfiguration.getRealm(), clientConfiguration);
                log.info("Client {} created with id {}.", clientConfiguration.getClientId(), id);
                createRolesIfNotExist(keycloak, realmConfiguration.getRealm(), id, clientConfiguration);
            } else {
                log.info("Client {} already existing.", clientConfiguration.getClientId());
            }
        }
    }

    private String createClient(Keycloak keycloak, String realm, ClientConfiguration clientConfiguration) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientConfiguration.getClientId());
        clientRepresentation.setEnabled(clientConfiguration.isEnabled());
        clientRepresentation.setStandardFlowEnabled(clientConfiguration.isStandardFlowEnabled());
        clientRepresentation.setDirectAccessGrantsEnabled(clientConfiguration.isDirectAccessGrantsEnabled());
        clientRepresentation.setRootUrl(clientConfiguration.getRootUrl());
        clientRepresentation.setBaseUrl(clientConfiguration.getBaseUrl());
        clientRepresentation.setProtocol(clientConfiguration.getProtocol());
        clientRepresentation.setAdminUrl(clientConfiguration.getAdminUrl());
        clientRepresentation.setRedirectUris(clientConfiguration.getValidRedirectUris());
        clientRepresentation.setWebOrigins(clientConfiguration.getWebOrigins());

        if(clientConfiguration.getImplicitFlowEnabled() != null)
            clientRepresentation.setImplicitFlowEnabled(clientConfiguration.getImplicitFlowEnabled());
        if(clientConfiguration.getAuthorizationServicesEnabled() != null)
            clientRepresentation.setAuthorizationServicesEnabled(clientConfiguration.getAuthorizationServicesEnabled());
        if(clientConfiguration.getPublicClient() != null)
            clientRepresentation.setPublicClient(clientConfiguration.getPublicClient());
        if(clientConfiguration.getServiceAccountEnabled() != null)
            clientRepresentation.setServiceAccountsEnabled(clientConfiguration.getServiceAccountEnabled());
        if(clientConfiguration.getAttributes() != null)
            clientRepresentation.setAttributes(clientConfiguration.getAttributes());

        keycloak.realm(realm).clients().create(clientRepresentation);

        return keycloak.realm(realm).clients().findByClientId(clientConfiguration.getClientId()).get(0).getId();
    }

    private void createRolesIfNotExist(Keycloak keycloak, String realm, String id, ClientConfiguration clientConfiguration) {
        List<RoleRepresentation> roles = keycloak.realm(realm).clients()
                .get(id).roles().list();
        for(RoleConfiguration roleConfiguration : clientConfiguration.getRoles()) {
            Optional<RoleRepresentation> role = roles.stream()
                    .filter(x -> x.getName().equals(roleConfiguration.getName())).findFirst();
            if(role.isEmpty()) {
                log.info("Role {} for client {} not existing, creating it.", roleConfiguration.getName(),
                        clientConfiguration.getClientId());
                createRole(keycloak, realm, id, roleConfiguration);
                log.info("Role {} for client {} created.", roleConfiguration.getName(),
                        clientConfiguration.getClientId());
            } else {
                log.info("Role {} for client {} already existing.", roleConfiguration.getName(),
                        clientConfiguration.getClientId());
            }
        }
    }

    private void createRole(Keycloak keycloak, String realm, String clientId, RoleConfiguration roleConfiguration) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(roleConfiguration.getName());
        roleRepresentation.setDescription(roleConfiguration.getDescription());
        roleRepresentation.setComposite(roleConfiguration.isComposite());
        if(roleConfiguration.isComposite()) {
            RoleRepresentation.Composites composites = new RoleRepresentation.Composites();
            if(roleConfiguration.getRealmComposite() != null && !roleConfiguration.getRealmComposite().isEmpty())
                composites.setRealm(roleConfiguration.getRealmComposite());
            if(roleConfiguration.getClientComposite() != null && !roleConfiguration.getClientComposite().isEmpty())
                composites.setClient(roleConfiguration.getClientComposite());
            roleRepresentation.setComposites(composites);
        }
        keycloak.realm(realm).clients().get(clientId).roles().create(roleRepresentation);
    }

    private void createUsersIfNotExist(Keycloak keycloak, RealmConfiguration realmConfiguration) {
        List<UserRepresentation> users = keycloak.realm(realmConfiguration.getRealm()).users().list();
        /*UserResource userRes = keycloak.realm(realmConfiguration.getRealm()).users().get("def81502-623c-45d6-b878-5f9bfc49de6b");
        List<RoleRepresentation> roles = userRes.roles().clientLevel("57ac1469-edc2-49b3-9841-ece1a6402ded").listAll();*/
        for(UserConfiguration userConfiguration : realmConfiguration.getUsers()) {
            Optional<UserRepresentation> user = users.stream()
                    .filter(x -> x.getUsername().equals(userConfiguration.getUsername())).findFirst();
            if(user.isEmpty()) {
                log.info("User {} in realm {} not existing, creating it.", userConfiguration.getUsername(),
                        realmConfiguration.getRealm());
                createUserIfNotExist(keycloak, realmConfiguration.getRealm(), userConfiguration);
                log.info("User {} in realm {} created.", userConfiguration.getUsername(),
                        realmConfiguration.getRealm());
            } else {
                log.info("User {} in realm {} already existing.", userConfiguration.getUsername(),
                        realmConfiguration.getRealm());
            }
        }
    }

    private void createUserIfNotExist(Keycloak keycloak, String realm, UserConfiguration userConfiguration) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userConfiguration.getUsername());
        userRepresentation.setEmail(userConfiguration.getEmail());
        userRepresentation.setFirstName(userConfiguration.getFirstname());
        userRepresentation.setLastName(userConfiguration.getLastname());
        userRepresentation.setEnabled(userConfiguration.isEnabled());
        userRepresentation.setEmailVerified(userConfiguration.isEmailVerified());

        if(userConfiguration.getRoles() != null && userConfiguration.getRoles().getRealm() != null)
            userRepresentation.setRealmRoles(userConfiguration.getRoles().getRealm());
        if(userConfiguration.getRoles() != null && userConfiguration.getRoles().getClients() != null)
            userRepresentation.setClientRoles(userConfiguration.getRoles().getClients());

        keycloak.realm(realm).users().create(userRepresentation);
        String id = keycloak.realm(realm).users().list().stream()
                .filter(x -> x.getUsername().equals(userConfiguration.getUsername())).findFirst().orElseThrow().getId();
        UserResource userResource = keycloak.realm(realm).users().get(id);

        if(userConfiguration.getRoles() != null && userConfiguration.getRoles().getRealm() != null) {
            userResource.roles().realmLevel().add(keycloak.realm(realm).roles()
                    .list().stream().filter(x -> userConfiguration.getRoles().getRealm().contains(x.getName()))
                    .collect(Collectors.toList()));
        }
        if(userConfiguration.getRoles() != null && userConfiguration.getRoles().getClients() != null) {
            for(Map.Entry<String, List<String>> entry : userConfiguration.getRoles().getClients().entrySet()) {
                String clientId = keycloak.realm(realm).clients().findByClientId(entry.getKey()).stream()
                        .findFirst().orElseThrow().getId();
                userResource.roles().clientLevel(clientId).add(keycloak.realm(realm).clients().get(clientId).roles()
                        .list().stream().filter(x -> entry.getValue().contains(x.getName()))
                        .collect(Collectors.toList()));
            }
        }

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(userConfiguration.isPasswordTemporary());
        credentialRepresentation.setType(userConfiguration.getPasswordType());
        credentialRepresentation.setValue(userConfiguration.getPasswordValue());
        userResource.resetPassword(credentialRepresentation);

        User entity = new User();

        entity.setKeycloakId(id);
        entity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        entity = userRepository.save(entity);
        userSettingsRepository.save(new UserSettings(entity, "{}"));

        AssetType type = assetTypeRepository.findByName("user").orElse(null);
        if (type == null) {
            log.warn("Asset type 'user' doesn't exists. It's being created");
            type = assetTypeRepository.save(new AssetType("user"));
        }

        Asset asset = new Asset();
        asset.setName(userConfiguration.getUsername());
        asset.setLabel(userConfiguration.getUsername());
        asset.setType(type);
        asset.setUser(entity);
        asset.setUuid(uuidRepository.saveAndFlush(new UUID()));

        assetRepository.save(asset);
    }
}
