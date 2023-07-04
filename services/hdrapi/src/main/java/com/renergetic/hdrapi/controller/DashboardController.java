package com.renergetic.hdrapi.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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

import com.renergetic.common.dao.DashboardDAO;
import com.renergetic.hdrapi.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "Dashboard Controller", description = "Allows add and see Dashboards")
@RequestMapping("/api/dashboard")
public class DashboardController {
	
	@Autowired
	DashboardService dashboardSv;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Dashboards")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<DashboardDAO>> getAllDashboards (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
		List<DashboardDAO> dashboards = new ArrayList<>();
		
		dashboards = dashboardSv.get(null, offset.orElse(0L), limit.orElse(20));
		
		return new ResponseEntity<>(dashboards, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Dashboards related with a User id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No dashboards found related with this user")
	})
	@GetMapping(path = "user/{user_id}", produces = "application/json")
	public ResponseEntity<List<DashboardDAO>> getDashboardsByUser (@PathVariable Long user_id){
		List<DashboardDAO> dashboards = new ArrayList<>();
		
		dashboards = dashboardSv.getByUser(user_id);
		
		dashboards = dashboards.isEmpty() ? null : dashboards;
		
		return new ResponseEntity<>(dashboards, dashboards != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Dashboard by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No dashboards found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<DashboardDAO> getDashboardsById (@PathVariable Long id){
		DashboardDAO dashboard = dashboardSv.getById(id);
		
		return new ResponseEntity<>(dashboard, dashboard != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Dashboard by id and test it", description = "Return requested dashboard and add a field (status) with the ping dashboard response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No dashboards found with this id"),
		@ApiResponse(responseCode = "405", description = "Dashboard server doesn't allow GET method"),
		@ApiResponse(responseCode = "500", description = "Error to try ping dashboard")
	})
	@GetMapping(path = "test/{id}", produces = "application/json")
	public ResponseEntity<DashboardDAO> getTestedDashboardsById (@PathVariable Long id){
		DashboardDAO dashboard = null;
		
		dashboard = dashboardSv.getById(id);
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(dashboard.getUrl()).openConnection();
			connection.setRequestMethod("GET");
			dashboard.setStatus(connection.getResponseCode());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		} catch (ProtocolException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
		return new ResponseEntity<>(dashboard, dashboard != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Dashboard")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Dashboard saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving dashboard")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<DashboardDAO> createDashboard(@RequestBody DashboardDAO dashboard) {
		DashboardDAO _dashboard = dashboardSv.save(dashboard);
		
		return new ResponseEntity<>(_dashboard, HttpStatus.CREATED);
	}

//=== PUT REQUESTS ====================================================================================

	@Operation(summary = "Update a existing Dashboard")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Dashboard saved correctly"),
			@ApiResponse(responseCode = "404", description = "Dashboard not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving dashboard")
		}
	)
	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<DashboardDAO> updateDashboard(@RequestBody DashboardDAO dashboard, @PathVariable Long id) {

		return new ResponseEntity<>(dashboardSv.update(dashboard,id), HttpStatus.OK);
	}

//=== DELETE REQUESTS =================================================================================
			
	@Operation(summary = "Delete a existing Dashboard")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Dashboard deleted correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving dashboard")
		}
	)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<?> deleteDashboard(@PathVariable Long id) {
		dashboardSv.deleteById(id);
		
		return ResponseEntity.noContent().build();
	}
}
