package com.renergetic.userapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.common.dao.UserDAORequest;
import com.renergetic.common.dao.UserDAOResponse;
import com.renergetic.common.model.security.KeycloakRole;
import com.renergetic.userapi.service.KeycloakService;
import com.renergetic.userapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Keycloak Controller", description = "Allows get and modify Keycloak and user resources")
@RequestMapping("/api/manage/user")
public class KeycloakController {
	
	@Autowired
	private KeycloakService keycloakSv;
	
	@Autowired
	private UserService userSv;
	
	@GetMapping("test")
	public ResponseEntity<Void> test() {
		return ResponseEntity.ok(null);
	}
	
	@Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<UserRepresentation>> getAllUsers(@RequestParam(required = false) Optional<String> role,
                                                                @RequestParam(required = false) Optional<Integer> offset,
                                                                @RequestParam(required = false) Optional<Integer> limit) {
		List<UserRepresentation> users = null;
		
        if (role.isPresent() && KeycloakRole.roleByName(role.get()) != null) {
            users = keycloakSv.listUsers(KeycloakRole.roleByName(role.get()).name, offset.orElse(0), limit.orElse(50));
        } else {
            users = keycloakSv.listUsers(offset.orElse(0), limit.orElse(50));
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get user roles")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/{userId}/roles", produces = "application/json")
    public ResponseEntity<List<String>> getRoles(@PathVariable String userId) {
        List<String> roles = keycloakSv.getRoles(userSv.translateDbIdToKeycloakId(userId))
        		.stream()
        		.map(RoleRepresentation::getName).collect(Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User saved correctly")
    @ApiResponse(responseCode = "500", description = "Error saving user")
    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserDAOResponse> createUser(@RequestBody UserDAORequest user) {
        UserRepresentation keycloakUser = keycloakSv.createUser(user);
        
        UserDAOResponse save = userSv.save(keycloakUser);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a existing User")
    @ApiResponse(responseCode = "200", description = "User saved correctly")
    @ApiResponse(responseCode = "404", description = "User not exist")
    @ApiResponse(responseCode = "422", description = "Type isn's valid")
    @ApiResponse(responseCode = "500", description = "Error saving user")
    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> updateUser(@PathVariable String userId, @RequestBody UserDAORequest user) {
        user.setId(userSv.translateDbIdToKeycloakId(user.getId()));
    	UserRepresentation keycloakUser = keycloakSv.updateUser(user);
        userSv.update(keycloakUser);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Operation(summary = "Add role to the User")
    @ApiResponse(responseCode = "200", description = "User saved correctly")
    @ApiResponse(responseCode = "404", description = "Role or User to associate not exist")
    @ApiResponse(responseCode = "422", description = "Type isn's valid")
    @ApiResponse(responseCode = "500", description = "Error saving user")
    @PutMapping(path = "/{id}/roles/{roleName}", produces = "application/json")
    public ResponseEntity<List<String>> addRole(@PathVariable String id, @PathVariable String roleName) {
        List<String> roles = keycloakSv.assignRole(userSv.translateDbIdToKeycloakId(id), roleName)
        		.stream()
        		.map(RoleRepresentation::getName)
        		.collect(Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    @Operation(summary = "Delete a existing User", hidden = false)
    @ApiResponse(responseCode = "200", description = "User deleted correctly")
    @ApiResponse(responseCode = "500", description = "Error deleting user")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        UserRepresentation userRepresentation = keycloakSv.getUser(userSv.translateDbIdToKeycloakId(id)).toRepresentation();
        keycloakSv.deleteUser(userRepresentation.getId());
        userSv.delete(userRepresentation);
        return ResponseEntity.ok(null);
    }


    @Operation(summary = "Revoke role", hidden = false)
    @ApiResponse(responseCode = "200", description = "Role deleted correctly")
    @ApiResponse(responseCode = "500", description = "Error deleting user")
    @DeleteMapping(path = "/{id}/roles/{roleName}")
    public ResponseEntity<List<String>> deleteRole(@PathVariable String id, @PathVariable String roleName) {
        List<String> roles = keycloakSv.revokeRole(userSv.translateDbIdToKeycloakId(id), roleName)
            .stream()
            .map(RoleRepresentation::getName)
            .collect(Collectors.toList());
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
