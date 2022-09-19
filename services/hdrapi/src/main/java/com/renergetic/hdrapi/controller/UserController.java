package com.renergetic.hdrapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.renergetic.hdrapi.dao.AssetDAOResponse;
import com.renergetic.hdrapi.dao.UserDAORequest;
import com.renergetic.hdrapi.dao.UserDAOResponse;
import com.renergetic.hdrapi.dao.UserRolesDAO;
import com.renergetic.hdrapi.dao.UserSettingsDAO;
import com.renergetic.hdrapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "User Controller", description = "Allows add and see Users")
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	UserService userSv;
	
//=== GET REQUESTS====================================================================================
	
	@Operation(summary = "Get All Users")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<UserDAOResponse>> getAllUsers (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<UserDAOResponse> users = new ArrayList<UserDAOResponse>();
		
		users = userSv.get(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@Operation(summary = "Get User by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Request executed correctly"),
			@ApiResponse(responseCode = "404", description = "No users found with this id")
	})
	@GetMapping(path = "id/{id}", produces = "application/json")
	public ResponseEntity<UserDAOResponse> getUsersById (@PathVariable Long id){
		UserDAOResponse user = null;

		user = userSv.getById(id);

		return new ResponseEntity<>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}	@Operation(summary = "Get current user profile")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Request executed correctly"),
			@ApiResponse(responseCode = "404", description = "No users found with this id")
	})
	@GetMapping(path = "/profile", produces = "application/json")
	public ResponseEntity<UserDAOResponse> getProfile ( ){
		//TODO: implement
		return null;
//		UserDAOResponse user = null;
//
//		user = userSv.getById(id);
//
//		return new ResponseEntity<>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get All Users Roles")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/roles", produces = "application/json")
	public ResponseEntity<List<UserRolesDAO>> getAllUsersRoles (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<UserRolesDAO> roles = new ArrayList<>();
		
		roles = userSv.getRoles(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(roles, HttpStatus.OK);
	}
	
	@Operation(summary = "Get All Users Settings")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/settings", produces = "application/json")
	public ResponseEntity<List<UserSettingsDAO>> getAllUsersSettings (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<UserSettingsDAO> settings = new ArrayList<>();
		
		settings = userSv.getSettings(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(settings, HttpStatus.OK);
	}

	@Operation (summary="Get Assets from User with specific id")
	@GetMapping(path ="/assets/{id}", produces="application/json")
	public ResponseEntity<List<AssetDAOResponse>> getAssets (@PathVariable Long id){
		List<AssetDAOResponse> assets = null;
		assets= userSv.getAssets(id);
		return new ResponseEntity<>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

	@Operation (summary="Get Assets from User with specific id and specific category id")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path ="/assets/{id}/{categoryId}", produces="application/json")
	public ResponseEntity<List<AssetDAOResponse>> getAssets (@PathVariable Long id, @PathVariable Long categoryId,
															 @RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<AssetDAOResponse> assets = null;
		assets= userSv.getAssetsByCategory(id, categoryId, offset.orElse(0L), limit.orElse(20));
		return new ResponseEntity<>(assets, assets != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
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
		try {	
			UserDAOResponse _user = userSv.save(user);
			return new ResponseEntity<>(_user, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Create a new User Role associated to a User")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Role saved correctly"),
			@ApiResponse(responseCode = "404", description = "User to assign role not found"),
			@ApiResponse(responseCode = "500", description = "Error saving role")
		}
	)
	@PostMapping(path = "/roles", produces = "application/json", consumes = "application/json")
	public ResponseEntity<UserRolesDAO> createUserRole(@RequestBody UserRolesDAO role) {
		try {	
			UserRolesDAO _role = userSv.saveRole(role);
			return new ResponseEntity<>(_role, _role != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Create a new User Setting associated to a User")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Setting saved correctly"),
			@ApiResponse(responseCode = "404", description = "User to assign role not found"),
			@ApiResponse(responseCode = "500", description = "Error saving role")
		}
	)
	@PostMapping(path = "/settings", produces = "application/json", consumes = "application/json")
	public ResponseEntity<UserSettingsDAO> createUserSetting(@RequestBody UserSettingsDAO role) {
		try {	
			UserSettingsDAO _role = userSv.saveSetting(role);
			return new ResponseEntity<>(_role, _role != null ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
	public ResponseEntity<UserDAOResponse> updateUser(@RequestBody UserDAORequest user, @PathVariable Long id) {
		try {
			user.setId(id);
			UserDAOResponse _user = userSv.update(user, id);
			return new ResponseEntity<>(_user, _user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Update a existing User Role")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User saved correctly"),
			@ApiResponse(responseCode = "404", description = "Role or User to associate not exist"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
			@ApiResponse(responseCode = "500", description = "Error saving user")
		}
	)
	@PutMapping(path = "/roles/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<UserRolesDAO> updateUserRoles(@RequestBody UserRolesDAO role, @PathVariable Long id) {
		try {
			role.setId(id);
			UserRolesDAO _role = userSv.updateRole(role, id);
			return new ResponseEntity<>(_role, _role != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
	public ResponseEntity<UserSettingsDAO> updateUserSettings(@RequestBody UserSettingsDAO setting, @PathVariable Long id) {
		try {
			setting.setId(id);
			UserSettingsDAO _setting = userSv.updateSetting(setting, id);
			return new ResponseEntity<>(_setting, _setting != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS ================================================================================
			
	@Operation(summary = "Delete a existing User", hidden = false)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "User deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error deleting user")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		try {
			userSv.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@Operation(summary = "Delete a existing User Role", hidden = false)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "User Role deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error deleting user role")
		}
	)
	@DeleteMapping(path = "/roles/{id}")
	public ResponseEntity<?> deleteUserRole(@PathVariable Long id) {
		try {
			userSv.deleteRoleById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@Operation(summary = "Delete a existing User Setting", hidden = false)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "User Setting deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error deleting user setting")
		}
	)
	@DeleteMapping(path = "/settings/{id}")
	public ResponseEntity<?> deleteUserSetting(@PathVariable Long id) {
		try {
			userSv.deleteSettingById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
