package com.renergetic.hdrapi.controller;

import com.renergetic.common.dao.*;
import com.renergetic.common.model.security.KeycloakRole;
import com.renergetic.common.model.security.KeycloakUser;
import com.renergetic.hdrapi.service.KeycloakService;
import com.renergetic.hdrapi.service.KeycloakWrapper;
import com.renergetic.hdrapi.service.LoggedInService;
import com.renergetic.hdrapi.service.NotificationService;
import com.renergetic.hdrapi.service.UserService;
import com.renergetic.hdrapi.service.utils.DummyDataGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.NotAuthorizedException;

@Deprecated // SEE USER API
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
    private DummyDataGenerator dummyDataGenerator;
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
                                                                @RequestParam(required = false) Optional<Integer> offset,
                                                                @RequestParam(required = false) Optional<Integer> limit) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        String token = loggedInService.getKeycloakUser().getToken();
        List<UserRepresentation> users;
        KeycloakWrapper client = keycloakService.getClient(token, true);
        int mOffset = offset.orElse(0);
        int mLimit = limit.orElse(50);
        if (role.isPresent() && KeycloakRole.roleByName(role.get()) != null) {
            users = client.listUsers(KeycloakRole.roleByName(role.get()).name, mOffset, mLimit);
        } else {
            users = client.listUsers(  mOffset, mLimit);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get user roles")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/{userId}/roles", produces = "application/json")
    public ResponseEntity<List<String>> getRoles(@PathVariable String userId) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        String token = loggedInService.getKeycloakUser().getToken();
        KeycloakWrapper client = keycloakService.getClient(token, true);
        try {
            List<String> roles = client.getRoles(userId).stream().map(RoleRepresentation::getName).collect(
                    Collectors.toList());
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }


    //#region PROFILE REQUESTS
    @Operation(summary = "Get current user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No users found with this id")
    })
    @GetMapping(path = "/profile", produces = "application/json")
    public ResponseEntity<UserDAOResponse> getProfile() {
        KeycloakUser user = loggedInService.getKeycloakUser();

        KeycloakWrapper client = keycloakService.getClient(user.getToken(), true);
        UserRepresentation keycloakProfile = client.getUser(user.getId()).toRepresentation();
        List<String> roles = client.getRoles(keycloakProfile.getId()).stream().map(RoleRepresentation::getName).collect(
                Collectors.toList());
        Long userId = loggedInService.getLoggedInUser().getId();
        String settingsJson = userSv.getSettings(userId);
        UserDAOResponse profile = UserDAOResponse.create(keycloakProfile, roles, settingsJson);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }


    @Operation(summary = "Get notifications for the current user")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/profile/notifications", produces = "application/json")
    public ResponseEntity<List<NotificationScheduleDAO>> getProfileNotifications(
            @RequestParam(value = "show_expired", required = false) Optional<Boolean> showExpired) {
        List<NotificationScheduleDAO> notifications;
        //TODO: filter by user id
        if (generateDummy) {
            notifications = dummyDataGenerator.getNotifications();
        } else {
            notifications = notificationSv.get(0L, 100, showExpired.orElse(false));
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @Operation(summary = "Get Current user Settings")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/profile/settings", produces = "application/json")
    public ResponseEntity<String> getAllUsersSettings() {
        Long userId = loggedInService.getLoggedInUser().getId();
        return new ResponseEntity<>(userSv.getSettings(userId), HttpStatus.OK);
    }

    @Operation(summary = "Set current user's settings")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Setting saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving role")})
    @PostMapping(path = "/profile/settings", produces = "application/json", consumes = "application/json")
    @PutMapping(path = "/profile/settings", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> saveUserSetting(@RequestBody String settings) {
        Long userId = loggedInService.getLoggedInUser().getId();
        settings = userSv.saveSettings(userId, settings);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    @Operation(summary = "Update current User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved correctly"),
            @ApiResponse(responseCode = "404", description = "User not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PutMapping(path = "/profile", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> updateProfile(@RequestBody UserDAORequest user) {
        KeycloakWrapper client = keycloakService.getClient(null, true);
        KeycloakUser keycloakUser = loggedInService.getKeycloakUser();
        user.setId(keycloakUser.getId());
        //TODO: synchronized section
        try {
            client.updateUser(user);
        } catch (javax.ws.rs.NotAuthorizedException ex) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        if (user.getPassword() != null) {
            client.updatePassword(keycloakUser.getId(), user.getPassword());
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Operation(summary = "Delete current user", hidden = false)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting user")})
    @DeleteMapping(path = "/profile")
    public ResponseEntity<?> deleteCurrentUser() {
        KeycloakWrapper client = keycloakService.getClient(null, true);
        KeycloakUser keycloakUser = loggedInService.getKeycloakUser();
        UserRepresentation userRepresentation = client.deleteUser(keycloakUser.getId());

        userSv.delete(userRepresentation);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    //#endregion

    //#region Admin GET REQUESTS

    //    TODO: to remove in the future releases
    @Operation(summary = "Get notifications for the current user")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/notifications", produces = "application/json")
    public ResponseEntity<List<NotificationScheduleDAO>> getUserNotifications(
            @RequestParam(required = false) Optional<Long> offset,
            @RequestParam(required = false) Optional<Integer> limit,
            @RequestParam(value = "show_expired", required = false) Optional<Boolean> showExpired) {
        List<NotificationScheduleDAO> notifications;
        //TODO: filter by user id
        if (generateDummy) {
            notifications = dummyDataGenerator.getNotifications();
        } else {
            notifications = notificationSv.get(offset.orElse(0L), limit.orElse(60), showExpired.orElse(false));
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @Operation(summary = "List all linked assets to the user")
    @GetMapping(path = "/assets/{userId}", produces = "application/json")
    public ResponseEntity<List<AssetDAOResponse>> getAssets(@PathVariable Long userId) {
        List<AssetDAOResponse> assets = null;
        assets = userSv.getAssets(userId);
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


    //#endregion

    //#region Admin POST/PUT REQUESTS

    @Operation(summary = "Create a new User")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User saved correctly"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserRepresentation> createUser(@RequestBody UserDAORequest user) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        KeycloakWrapper client = keycloakService.getClient(loggedInService.getKeycloakUser().getToken(), true);
        UserRepresentation ur = client.createUser(user);
        user.setId(ur.getId());
        UserDAOResponse save = userSv.save(ur, user);
        return new ResponseEntity<>(ur, HttpStatus.CREATED);
    }

    @Operation(summary = "Modify user's settings")
    @PostMapping(path = "/settings/user", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserSettingsDAO> createUserSetting(@RequestBody UserSettingsDAO UserDAORequest) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig

        UserSettingsDAO _settings = userSv.saveSetting(UserDAORequest);
        return new ResponseEntity<>(_settings, _settings != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update a existing User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved correctly"),
            @ApiResponse(responseCode = "404", description = "User not exist"),
            @ApiResponse(responseCode = "422", description = "Type isn's valid"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> updateUser(@RequestBody UserDAORequest user, @PathVariable String id) {
        loggedInService.hasRole(
                KeycloakRole.REN_ADMIN.mask | KeycloakRole.REN_TECHNICAL_MANAGER.mask);//TODO: WebSecurityConfig
        KeycloakWrapper client = keycloakService.getClient(loggedInService.getKeycloakUser().getToken(), true);
        //TODO: synchronized section
        try {
            client.updateUser(user);
        } catch (NotAuthorizedException ex) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        UserRepresentation ur = client.updateUser(user);
        userSv.update(user, ur);//TODO: return respoonse of the user ?
        return new ResponseEntity<>(true, HttpStatus.OK);
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
    @PutMapping(path = "/{id}/roles/{roleName}", produces = "application/json")
    public ResponseEntity<List<String>> addRole(@PathVariable String id, @PathVariable String roleName) {
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        String token = loggedInService.getKeycloakUser().getToken();
        KeycloakWrapper client = keycloakService.getClient(token, true);
        List<String> roles = client.assignRole(id, roleName).stream().map(RoleRepresentation::getName).collect(
                Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    //TODO: is it used anywhere ?
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
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        setting.setId(id);//todo
        UserSettingsDAO _setting = userSv.updateSettings(setting, id);
        return new ResponseEntity<>(_setting, _setting != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    //#endregion

    //#region Admin DELETE REQUESTS
    @Operation(summary = "Delete an existing User", hidden = false)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting user")})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        if (Objects.equals(loggedInService.getKeycloakUser().getId(), id)) {
            throw new RuntimeException("Cannot self delete user");
        }
        String token = loggedInService.getKeycloakUser().getToken();
        KeycloakWrapper client = keycloakService.getClient(token, true);
        boolean isAdmin = client.getRoles(id).stream().filter(
                it -> it.getName().equals(KeycloakRole.REN_ADMIN.name)).findAny().isEmpty();
        if (isAdmin) {
            throw new RuntimeException("Cannot delete admin");
        }
        UserRepresentation userRepresentation = client.getUser(id).toRepresentation();
        client.deleteUser(userRepresentation.getId());
        userSv.delete(userRepresentation);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @Operation(summary = "Revoke role", hidden = false)
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Role deleted correctly"),
            @ApiResponse(responseCode = "500", description = "Error deleting user")})
    @DeleteMapping(path = "/{id}/roles/{roleName}")
    public ResponseEntity<?> deleteRole(@PathVariable String id, @PathVariable String roleName) {
        loggedInService.hasRole(KeycloakRole.REN_ADMIN.mask);//TODO: WebSecurityConfig
        if (Objects.equals(loggedInService.getKeycloakUser().getId(), id) && roleName.equals(KeycloakRole.REN_ADMIN.name)) {
            throw new RuntimeException("Cannot self remove admin role");
        }
        String token = loggedInService.getKeycloakUser().getToken();
        KeycloakWrapper client = keycloakService.getClient(token, true);
        List<String> roles = client.revokeRole(id, roleName).stream().map(RoleRepresentation::getName).collect(
                Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    //#endregion

}
