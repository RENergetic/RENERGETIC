package com.renergetic.hdrapi.controller;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.model.security.KeycloakAuthenticationToken;
import com.renergetic.hdrapi.model.security.KeycloakRole;
import com.renergetic.hdrapi.service.KeycloakService;
import com.renergetic.hdrapi.service.LoggedInService;
import com.renergetic.hdrapi.service.NotificationService;
import com.renergetic.hdrapi.service.UserService;
import com.renergetic.hdrapi.service.utils.DummyDataGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "User Controller", description = "Allows add and see Users")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userSv;
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
    @Autowired
    NotificationService notificationSv;
    @Autowired
    LoggedInService loggedInService;
    @Autowired
    KeycloakService keycloakService;

//=== GET REQUESTS====================================================================================


    @Operation(summary = "Get All Users")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<UserRepresentation>> getAllUsers(@RequestParam(required = false) Optional<String> role,
                                                                @RequestParam(required = false) Optional<Long> offset,
                                                                @RequestParam(required = false) Optional<Integer> limit) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        var token = loggedInService.getKeycloakUser().getToken();
        List<UserRepresentation> users;
        var client = keycloakService.getClient(token,true);

        if (role.isPresent() && KeycloakRole.roleByName(role.get()) != null) {
            users =  client.listUsers(KeycloakRole.roleByName(role.get()).name);
        } else {
            users = client.listUsers();
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get user roles")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/{userId}/roles", produces = "application/json")
    public ResponseEntity<List<String>> getRoles(@PathVariable String userId) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        var token = loggedInService.getKeycloakUser().getToken();
        var client = keycloakService.getClient(token, true);
        try {
            List<String> roles = client.getRoles(userId).stream().map(RoleRepresentation::getName).collect(
                    Collectors.toList());
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (javax.ws.rs.NotAuthorizedException ex) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }


    @Operation(summary = "Get current user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No users found with this id")
    })
    @GetMapping(path = "/profile", produces = "application/json")
    public ResponseEntity<UserDAOResponse> getProfile() {
        //TODO: implement
        return null;
//		UserDAOResponse user = null;
//
//		user = userSv.getById(id);
//
//		return new ResponseEntity<>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
//
//    @Operation(summary = "Get All Users Roles")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @GetMapping(path = "/roles", produces = "application/json")
//    public ResponseEntity<List<UserRolesDAO>> getAllUsersRoles(@RequestParam(required = false) Optional<Long> offset,
//                                                               @RequestParam(required = false) Optional<Integer> limit) {
//        List<UserRolesDAO> roles = new ArrayList<>();
//
//        roles = userSv.getRoles(null, offset.orElse(0L), limit.orElse(20));
//
//        return new ResponseEntity<>(roles, HttpStatus.OK);
//    }


    @Operation(summary = "Get All Notifications for the user")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/notifications", produces = "application/json")
    public ResponseEntity<List<NotificationDAO>> getUserNotifications(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(value = "show_expired", required = false) Optional<Boolean> showExpired) {
        List<NotificationDAO> notifications;
        //TODO: filter by user id
        if (generateDummy) {
            notifications = DummyDataGenerator.getNotifications();
        } else {
            notifications = notificationSv.get(offset.orElse(0L), limit.orElse(60), showExpired.orElse(false));
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @Operation(summary = "Get Assets from User with specific id")
    @GetMapping(path = "/assets/{id}", produces = "application/json")
    public ResponseEntity<List<AssetDAOResponse>> getAssets(@PathVariable Long id) {
        List<AssetDAOResponse> assets = null;
        assets = userSv.getAssets(id);
        return new ResponseEntity<>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get Assets from User with specific id and specific category id")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/assets/{id}/{categoryId}", produces = "application/json")
    public ResponseEntity<List<AssetDAOResponse>> getAssets(@PathVariable Long id, @PathVariable Long categoryId,
                                                            @RequestParam(required = false) Optional<Long> offset,
                                                            @RequestParam(required = false) Optional<Integer> limit) {
        List<AssetDAOResponse> assets = null;
        assets = userSv.getAssetsByCategory(id, categoryId, offset.orElse(0L), limit.orElse(20));
        return new ResponseEntity<>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get User Settings")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/profile/settings", produces = "application/json")
    public ResponseEntity<String> getAllUsersSettings() {
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Object authorities = authentication.getAuthorities();
        authentication.getUser();

//        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_STAFF.mask);
        Long userId = 2l;    //TODO: infer from headers
        return new ResponseEntity<>(userSv.getSettings(userId), HttpStatus.OK);
    }

//=== POST REQUESTS ===================================================================================

    @Operation(summary = "Create a new User")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserDAOResponse> createUser(@RequestBody UserDAORequest user) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        var client = keycloakService.getClient(true);
        UserRepresentation ur = client.createUser(user);
        user.setId(ur.getId());
        UserDAOResponse save = userSv.save(user);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

//    @Operation(summary = "Create a new User Role associated to a User")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Role saved correctly"),
//            @ApiResponse(responseCode = "404", description = "User to assign role not found"),
//            @ApiResponse(responseCode = "500", description = "Error saving role")
//    }
//    )
//    @PostMapping(path = "/roles", produces = "application/json", consumes = "application/json")
//    public ResponseEntity<UserRolesDAO> createUserRole(@RequestBody UserRolesDAO role) {
//        UserRolesDAO _role = userSv.saveRole(role);
//        return new ResponseEntity<>(_role, _role != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
//    }

    @Operation(summary = "Create a new User Setting associated to a User")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Setting saved correctly"),
            @ApiResponse(responseCode = "404", description = "User to assign role not found"),
            @ApiResponse(responseCode = "500", description = "Error saving role")
    }
    )
    @PostMapping(path = "/profile/settings", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> saveUserSetting(@RequestBody String settings) {
        Long userId = 2l;    //TODO: infer from headers
        settings = userSv.saveSettings(userId, settings);

        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    @PostMapping(path = "/settings/user", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserSettingsDAO> createUserSetting(@RequestBody UserSettingsDAO role) {
        UserSettingsDAO _role = userSv.saveSetting(role);
        return new ResponseEntity<>(_role, _role != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
    }

//=== PUT REQUESTS====================================================================================

    @Operation(summary = "Update a existing User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved correctly"),
            @ApiResponse(responseCode = "404", description = "User not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity updateUser(@RequestBody UserDAORequest user, @PathVariable String id) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        var client = keycloakService.getClient(true);
        //TODO: synchronized section
        UserRepresentation ur = client.updateUser(user);
        userSv.update(user, ur);
        return new ResponseEntity<>(null, HttpStatus.OK);
//        return new ResponseEntity<>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);//todo trow not found
    }

//    @Operation(summary = "Update a existing User")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "User saved correctly"),
//            @ApiResponse(responseCode = "404", description = "User not exist"),
//            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
//            @ApiResponse(responseCode = "500", description = "Error saving user")
//    }
//    )
//    @PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
//    public ResponseEntity<UserDAOResponse> updateUser(@RequestBody UserDAORequest user, @PathVariable Long id) {
//        user.setId(id);
//        UserDAOResponse _user = userSv.update(user, id);
//        return new ResponseEntity<>(_user, _user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
//    }

    @Operation(summary = "Add role to the User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved correctly"),
            @ApiResponse(responseCode = "404", description = "Role or User to associate not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PutMapping(path = "/{userId}/roles/{roleName}", produces = "application/json")
    public ResponseEntity<List<String>> addRole(@PathVariable String userId, @PathVariable String roleName) {
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        var token = loggedInService.getKeycloakUser().getToken();
        var client = keycloakService.getClient(token, true);
        List<String> roles = client.assignRole(userId, roleName).stream().map(RoleRepresentation::getName).collect(
                Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Update a existing User Setting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved correctly"),
            @ApiResponse(responseCode = "404", description = "Setting or User to associate not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PutMapping(path = "/settings/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserSettingsDAO> updateUserSettings(@RequestBody UserSettingsDAO setting,
                                                              @PathVariable Long id) {
        setting.setId(id);//todo
        UserSettingsDAO _setting = userSv.updateSettings(setting, id);
        return new ResponseEntity<>(_setting, _setting != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    //=== DELETE REQUESTS ================================================================================
    @Operation(summary = "Delete a existing User", hidden = false)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting user")})
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity deleteUser(@PathVariable String userId) {
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        var token = loggedInService.getKeycloakUser().getToken();
        var client = keycloakService.getClient(token, true);
        UserRepresentation userRepresentation = client.getUser(userId).toRepresentation();
        client.deleteUser(userRepresentation.getId());
        UserDAOResponse user = userSv.delete(userRepresentation);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @Operation(summary = "Revoke role", hidden = false)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Role deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting user")})
    @DeleteMapping(path = "/{userId}/roles/{roleName}")
    public ResponseEntity<?> deleteRole(@PathVariable String userId, @PathVariable String roleName) {
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        var token = loggedInService.getKeycloakUser().getToken();
        var client = keycloakService.getClient(token, true);
        List<String> roles = client.revokeRole(userId, roleName).stream().map(RoleRepresentation::getName).collect(
                Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }


}

//    @Operation(summary = "Delete a existing User Role", hidden = false)
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "User Role deleted correctly"),
//            @ApiResponse(responseCode = "500", description = "Error deleting user role")
//    }
//    )
//    @DeleteMapping(path = "/roles/{id}")
//    public ResponseEntity<?> deleteUserRole(@PathVariable Long id) {
//        userSv.deleteRoleById(id);
//
//        return ResponseEntity.noContent().build();
//    }
//    @Operation(summary = "Delete a existing User Setting", hidden = false)
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "User Setting deleted correctly"),
//            @ApiResponse(responseCode = "500", description = "Error deleting user setting")
//    }
//    )
//    @DeleteMapping(path = "/settings/{id}")
//    public ResponseEntity<?> deleteUserSetting(@PathVariable Long id) {
//        userSv.deleteSettingById(id);
//
//        return ResponseEntity.noContent().build();
//    }
//    @Operation(summary = "Get All Users Settings")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @GetMapping(path = "/settings", produces = "application/json")
//    public ResponseEntity<List<UserSettingsDAO>> getAllUsersSettings(
//            @RequestParam(required = false) Optional<Long> offset,
//            @RequestParam(required = false) Optional<Integer> limit) {
//        List<UserSettingsDAO> settings = new ArrayList<>();
//
//        settings = userSv.getSettings(null, offset.orElse(0L), limit.orElse(20));
//
//        return new ResponseEntity<>(settings, HttpStatus.OK);
//    }
//    @Operation(summary = "Get All Users")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @GetMapping(path = "", produces = "application/json")
//    public ResponseEntity<List<UserDAOResponse>> getAllUsers(@RequestParam(required = false) Optional<Long> offset,
//                                                             @RequestParam(required = false) Optional<Integer> limit) {
//        List<UserDAOResponse> users = new ArrayList<UserDAOResponse>();
//
//        users = userSv.get(null, offset.orElse(0L), limit.orElse(20));
//
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }//    @Operation(summary = "Get User by id")
////    @ApiResponses({
////            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
////            @ApiResponse(responseCode = "404", description = "No users found with this id")
////    })
////    @GetMapping(path = "id/{id}", produces = "application/json")
////    public ResponseEntity<UserDAOResponse> getUsersById(@PathVariable Long id) {
////        UserDAOResponse user = null;
////
////        user = userSv.getById(id);
////
////        return new ResponseEntity<>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
////    }