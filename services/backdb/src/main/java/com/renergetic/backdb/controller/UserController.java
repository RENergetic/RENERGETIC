package com.renergetic.backdb.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Connection;
import com.renergetic.backdb.model.Information;
import com.renergetic.backdb.model.User;
import com.renergetic.backdb.model.UserRequest;
import com.renergetic.backdb.model.information.UserInformation;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.UserRepository;
import com.renergetic.backdb.repository.information.UserInformationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Users Controller", description = "Allows add and see Users")
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserInformationRepository informationRepository;
	@Autowired
	AssetRepository assetRepository;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Users")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<User>> getAllUsers (){
		List<User> users = new ArrayList<User>();
		
		users = userRepository.findAll();
		
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Users by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No users found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<User>> getUsersByName (@PathVariable String name){
		List<User> users = new ArrayList<User>();
		
		users = userRepository.findByName(name);
		
		users = users.isEmpty() ? null : users;
		
		return new ResponseEntity<List<User>>(users, users != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get User by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No users found with this id")
	})
	@GetMapping(path = "id/{id}", produces = "application/json")
	public ResponseEntity<User> getUsersByLocation (@PathVariable Long id){
		User user = null;
		
		user = userRepository.findById(id).orElse(null);
		
		return new ResponseEntity<User>(user, user != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Information from a User")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "User havent information or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<UserInformation>> getInformationAsset (@PathVariable Long id){
		List<UserInformation> info = null;
		
		info = informationRepository.findByUserId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<UserInformation>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Information from a User and its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "User havent information or doesn't exists")
	})
	@GetMapping(path = "{user_id}/info/{info_id}", produces = "application/json")
	public ResponseEntity<UserInformation> getInformationByID (@PathVariable Long user_id, @PathVariable Long info_id){
		UserInformation info = null;
		
		info = informationRepository.findByIdAndUserId(info_id, user_id);
		
		return new ResponseEntity<UserInformation>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Information for a User")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Information saved correctly"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PostMapping(path = "{user_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<UserInformation> insertInformation (@RequestBody UserInformation information, @PathVariable Long user_id){
		try {
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information.setUserId(user_id);
				information = informationRepository.save(information);
				return new ResponseEntity<>(information, HttpStatus.CREATED);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Update Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Information saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Information not exist"),
			@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{user_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<UserInformation> updateInformation (@RequestBody UserInformation information, @PathVariable Long user_id){
		try {
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information = informationRepository.save(information);
				return new ResponseEntity<>(information, HttpStatus.OK);
			} else return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Operation(summary = "Delete Information from its id")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Information delete"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{user_id}/info/{info_id}")
	public ResponseEntity<UserInformation> updateInformation (@PathVariable Long user_id, @PathVariable Long info_id){
		try {
			informationRepository.deleteById(info_id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new User")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving user")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<User> createUser(@RequestBody UserRequest user) {
		try {
			User _user = userRepository.save(user.mapToEntity());
			return new ResponseEntity<>(_user, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================
			
	@Operation(summary = "Update a existing User")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User saved correctly"),
			@ApiResponse(responseCode = "400", description = "Path isn't valid"),
			@ApiResponse(responseCode = "404", description = "User not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving user")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<User> updateUser(@RequestBody UserRequest user, @PathVariable Long id) {
		try {
			user.setId(id);
			if (userRepository.update(user.mapToEntity(), id) == 0)
				return ResponseEntity.notFound().build();
			else
				return new ResponseEntity<>(user.mapToEntity(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing User", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "User deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving user")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		try {
			userRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
//=== USER RESOURCER REQUESTS =========================================================================

	@Operation(summary = "Get user residence asset")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Cannot get residence asset")
	})
	@GetMapping(path = "/{id}/home", produces = "application/json")
	public ResponseEntity<Asset> getUserHome(@PathVariable Long id) {
		try {
			Asset asset = userRepository.findResidence(id);
			
			return new ResponseEntity<Asset>(asset, asset != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Get user residence connections")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Cannot get residence asset")
	})
	@GetMapping(path = "/{id}/home/connections", produces = "application/json")
	public ResponseEntity<List<Connection>> getHomeConnections(@PathVariable Long id) {
		try {
			List<Connection> connections = assetRepository.findConnections(userRepository.findById(id).get().getResideAsset().getId());
			
			if (connections.size() == 0)
				connections = null;
			
			return new ResponseEntity<List<Connection>>(connections, connections != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
