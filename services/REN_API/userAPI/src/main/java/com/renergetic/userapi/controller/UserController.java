package com.renergetic.userapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.userapi.dao.UserDAORequest;
import com.renergetic.userapi.dao.UserDAOResponse;
import com.renergetic.userapi.model.security.KeycloakUser;
import com.renergetic.userapi.service.KeycloakService;
import com.renergetic.userapi.service.LoggedInService;
import com.renergetic.userapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "User Controller", description = "Allows get and modify logged in user information")
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	KeycloakService keycloakSv;
	
	@Autowired
	UserService userSv;
	
	@Autowired
	private LoggedInService loggedInSv;
	
	@GetMapping("test")
	public ResponseEntity<?> test() {
		return ResponseEntity.ok(null);
	}
	
	// GET REQUESTS

    @Operation(summary = "Get current user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request executed correctly"),
            @ApiResponse(responseCode = "404", description = "No users found with this id")
    })
    @GetMapping(path = "/profile", produces = "application/json")
    public ResponseEntity<UserDAOResponse> getProfile() {
        KeycloakUser user = loggedInSv.getAuthenticationData().getPrincipal();

        UserRepresentation keycloakProfile = keycloakSv.getUser(user.getId()).toRepresentation();
        List<String> roles = keycloakSv.getRoles(keycloakProfile.getId())
        		.stream()
        		.map(RoleRepresentation::getName)
        		.collect(Collectors.toList());
        
        String settingsJson = userSv.getSettings(user.getId()).getSettingsJson();
        UserDAOResponse profile = UserDAOResponse.create(keycloakProfile, roles, settingsJson);
        
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @Operation(summary = "Get current user's settings")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/profile/settings", produces = "application/json")
    public ResponseEntity<String> getAllUsersSettings() {
        String userId = loggedInSv.getAuthenticationData().getPrincipal().getId();
        return new ResponseEntity<>(userSv.getSettings(userId).getSettingsJson(), HttpStatus.OK);
    }
    
    // POST REQUESTS

    @Operation(summary = "Create a new user setting associated to the logged user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Setting saved correctly"),
            @ApiResponse(responseCode = "404", description = "User to assign settings not found"),
            @ApiResponse(responseCode = "500", description = "Error saving settings")
    }
    )
    @PostMapping(path = "/profile/settings", produces = "application/json", consumes = "application/json")
    @PutMapping(path = "/profile/settings", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> saveUserSetting(@RequestBody String settings) {
        String userId = loggedInSv.getAuthenticationData().getPrincipal().getId();
        settings = userSv.saveSettings(userId, settings).getSettingsJson();

        return new ResponseEntity<>(settings, HttpStatus.CREATED);
    }
    
    // PUT REQUESTS

    @Operation(summary = "Update current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved correctly"),
            @ApiResponse(responseCode = "404", description = "User not exist"),
            @ApiResponse(responseCode = "500", description = "Error saving user")
    }
    )
    @PutMapping(path = "/profile", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> updateProfile(@RequestBody UserDAORequest user) {
        KeycloakUser keycloakUser = loggedInSv.getAuthenticationData().getPrincipal();
        user.setId(keycloakUser.getId());

        keycloakSv.updateUser(user);
        if (user.getPassword() != null) {
            keycloakSv.updatePassword(keycloakUser.getId(), user.getPassword());
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}