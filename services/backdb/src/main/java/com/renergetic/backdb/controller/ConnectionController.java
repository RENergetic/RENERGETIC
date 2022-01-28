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

import com.renergetic.backdb.model.Connection;
import com.renergetic.backdb.model.ConnectionRequest;
import com.renergetic.backdb.model.Information;
import com.renergetic.backdb.model.Infrastructure;
import com.renergetic.backdb.model.information.ConnectionInformation;
import com.renergetic.backdb.repository.ConnectionRepository;
import com.renergetic.backdb.repository.InfrastructureRepository;
import com.renergetic.backdb.repository.information.ConnectionInformationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Connection Controller", description = "Allows add and see Connections")
@RequestMapping("/api/connections")
public class ConnectionController {
	
	@Autowired
	ConnectionRepository connectionRepository;
	@Autowired
	ConnectionInformationRepository informationRepository;
	@Autowired
	InfrastructureRepository infrastructureRepository;

//=== GET REQUESTS ====================================================================================
		
	@Operation(summary = "Get All Connections")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Connection>> getAllConnections (){
		List<Connection> connections = new ArrayList<Connection>();
		
		connections = connectionRepository.findAll();
		
		return new ResponseEntity<List<Connection>>(connections, HttpStatus.OK);
	}
	
	@Operation(summary = "Get connections by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No connections found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Connection>> getConnectionsByName (@PathVariable String name){
		List<Connection> connections = new ArrayList<Connection>();
		
		connections = connectionRepository.findByName(name);
		
		connections = connections.isEmpty() ? null : connections;
		
		return new ResponseEntity<List<Connection>>(connections, connections != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Connections by location")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No connections found of this type")
	})
	@GetMapping(path = "asset/{asset_id}", produces = "application/json")
	public ResponseEntity<List<Connection>> getConnectionsByType (@PathVariable String asset_id){
		List<Connection> connections = new ArrayList<Connection>();
		
		connections = connectionRepository.findByAssetId(asset_id);
		
		connections = connections.isEmpty() ? null : connections;
		
		return new ResponseEntity<List<Connection>>(connections, connections != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Connection by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No connections found with this id")
	})
	@GetMapping(path = "connection/{id}", produces = "application/json")
	public ResponseEntity<Connection> getConnectionsById (@PathVariable Long id){
		Connection connection = null;
		
		connection = connectionRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Connection>(connection, connection != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Input Infrastructure by conection id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No connections found with this id")
	})
	@GetMapping(path = "input/infrastructure/{id}", produces = "application/json")
	public ResponseEntity<Infrastructure> getInputInfrastucture (@PathVariable Long id){
		Connection connection = null;
		Infrastructure infrastructure = null;
		
		connection = connectionRepository.findById(id).orElse(null);
		
		if (connection != null)
			 infrastructure = infrastructureRepository.findById(connection.getInputInfrastructure().getId()).orElse(null);
		
		return new ResponseEntity<Infrastructure>(infrastructure, infrastructure != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Output Infrastructure by conection id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No connections found with this id")
	})
	@GetMapping(path = "output/infrastructure/{id}", produces = "application/json")
	public ResponseEntity<Infrastructure> getOutputInfrastucture (@PathVariable Long id){
		Connection connection = null;
		Infrastructure infrastructure = null;
		
		connection = connectionRepository.findById(id).orElse(null);
		
		if (connection != null)
			 infrastructure = infrastructureRepository.findById(connection.getOutputInfrastructure().getId()).orElse(null);
		
		return new ResponseEntity<Infrastructure>(infrastructure, infrastructure != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== INFO REQUESTS ===================================================================================
		
	@Operation(summary = "Get Information from a Connection")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Connection havent information or doesn't exists")
	})
	@GetMapping(path = "{id}/info", produces = "application/json")
	public ResponseEntity<List<ConnectionInformation>> getInformationAsset (@PathVariable Long id){
		List<ConnectionInformation> info = null;
		
		info = informationRepository.findByConnectionId(id);
		
		info = info.isEmpty() ? null : info;
		
		return new ResponseEntity<List<ConnectionInformation>>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Information from a Connection and its id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "Connection havent information or doesn't exists")
	})
	@GetMapping(path = "{connection_id}/info/{info_id}", produces = "application/json")
	public ResponseEntity<ConnectionInformation> getInformationByID (@PathVariable Long connection_id, @PathVariable Long info_id){
		ConnectionInformation info = null;
		
		info = informationRepository.findByIdAndConnectionId(info_id, connection_id);
		
		return new ResponseEntity<ConnectionInformation>(info, info != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Insert Information for a Connection")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Information saved correctly"),
		@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PostMapping(path = "{connection_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ConnectionInformation> insertInformation (@RequestBody ConnectionInformation information, @PathVariable Long connection_id){
		try {
			if (Information.ALLOWED_TYPES.stream().anyMatch(information.getType()::equalsIgnoreCase)) {
				information.setConnectionId(connection_id);
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
		@ApiResponse(responseCode = "201", description = "Information saved correctly"),
		@ApiResponse(responseCode = "404", description = "Information not exist"),
		@ApiResponse(responseCode = "422", description = "Type isn's valid"),
		@ApiResponse(responseCode = "500", description = "Error saving information")
	})
	@PutMapping(path = "{connection_id}/info", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ConnectionInformation> updateInformation (@RequestBody ConnectionInformation information, @PathVariable Long connection_id){
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
		@ApiResponse(responseCode = "204", description = "Information deletec"),
		@ApiResponse(responseCode = "500", description = "Error deleting information")
	})
	@DeleteMapping(path = "{connection_id}/info/{info_id}")
	public ResponseEntity<ConnectionInformation> updateInformation (@PathVariable Long connection_id, @PathVariable Long info_id){
		try {
			informationRepository.deleteById(info_id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Connection")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Connection saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving connection")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Connection> createConnection(@RequestBody ConnectionRequest connection) {
		try {
			Connection _connection = connectionRepository.save(connection.mapToEntity());
			return new ResponseEntity<>(_connection, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================
			
	@Operation(summary = "Update a existing Connection")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Connection saved correctly"),
		@ApiResponse(responseCode = "400", description = "Path isn't valid"),
		@ApiResponse(responseCode = "404", description = "Connection not exist"),
		@ApiResponse(responseCode = "500", description = "Error saving connection")
	})
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Connection> updateConnection(@RequestBody ConnectionRequest connection, @PathVariable Long id) {
		try {
			connection.setId(id);
			if (connectionRepository.update(connection.mapToEntity(), id) == 0)
				return ResponseEntity.notFound().build();
			else
				return new ResponseEntity<>(connection.mapToEntity(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== DELETE REQUESTS =================================================================================
	
	@Operation(summary = "Delete a existing Connection", hidden = true)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Connection deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error deleting connection")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteConnection(@PathVariable Long id) {
		try {
			connectionRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
