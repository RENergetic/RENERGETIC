package com.renergetic.backdb.controller;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;

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

import com.renergetic.backdb.model.Dashboard;
import com.renergetic.backdb.repository.DashboardRepository;

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
	DashboardRepository dashboardRepository;

//=== GET REQUESTS ====================================================================================
			
	@Operation(summary = "Get All Dashboards")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "", produces = "application/json")
	public ResponseEntity<List<Dashboard>> getAllDashboards (){
		List<Dashboard> dashboards = new ArrayList<Dashboard>();
		
		dashboards = dashboardRepository.findAll();
		
		return new ResponseEntity<List<Dashboard>>(dashboards, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Dashboards by name")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No dashboards found with this name")
	})
	@GetMapping(path = "name/{name}", produces = "application/json")
	public ResponseEntity<List<Dashboard>> getDashboardsByName (@PathVariable String name){
		List<Dashboard> dashboards = new ArrayList<Dashboard>();
		
		dashboards = dashboardRepository.findByName(name);
		
		dashboards = dashboards.isEmpty() ? null : dashboards;
		
		return new ResponseEntity<List<Dashboard>>(dashboards, dashboards != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Dashboards by URL")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No dashboards found in this URL")
	})
	@GetMapping(path = "url/{url}", produces = "application/json")
	public ResponseEntity<List<Dashboard>> getDashboardsByURL (@PathVariable @Pattern(regexp = "https?://\\\\S+([/?].+)?", message = "URL isn't valid format") String url){
		List<Dashboard> dashboards = new ArrayList<Dashboard>();
		
		dashboards = dashboardRepository.findByUrl(url);
		
		dashboards = dashboards.isEmpty() ? null : dashboards;
		
		return new ResponseEntity<List<Dashboard>>(dashboards, dashboards != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Dashboard by id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "400", description = "Malformed URL"),
		@ApiResponse(responseCode = "404", description = "No dashboards found with this id")
	})
	@GetMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<Dashboard> getDashboardsById (@PathVariable Long id){
		Dashboard dashboard = null;
		
		dashboard = dashboardRepository.findById(id).orElse(null);
		
		return new ResponseEntity<Dashboard>(dashboard, dashboard != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	
	@Operation(summary = "Get Dashboard by id and test it", description = "Return requested dashboard and add a field (status) with the ping dashboard response")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
		@ApiResponse(responseCode = "404", description = "No dashboards found with this id"),
		@ApiResponse(responseCode = "500", description = "Error to try ping dashboard")
	})
	@GetMapping(path = "test/{id}", produces = "application/json")
	public ResponseEntity<Dashboard> getTestedDashboardsById (@PathVariable Long id){
		Dashboard dashboard = null;
		
		dashboard = dashboardRepository.findById(id).orElse(null);
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(dashboard.getUrl()).openConnection();
			connection.setRequestMethod("GET");
			dashboard.setStatus(connection.getResponseCode());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
		return new ResponseEntity<Dashboard>(dashboard, dashboard != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}

//=== POST REQUESTS ===================================================================================
			
	@Operation(summary = "Create a new Dashboard")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Dashboard saved correctly"),
			@ApiResponse(responseCode = "500", description = "Error saving dashboard")
		}
	)
	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Dashboard> createDashboard(@RequestBody Dashboard dashboard) {
		try {
			dashboard.setId(null);
			Dashboard _dashboard = dashboardRepository.save(dashboard);
			
			return new ResponseEntity<>(_dashboard, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//=== PUT REQUESTS ====================================================================================

	@Operation(summary = "Update a existing Dashboard")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Dashboard saved correctly"),
			@ApiResponse(responseCode = "404", description = "Dashboard not exist"),
			@ApiResponse(responseCode = "500", description = "Error saving dashboard")
		}
	)
	@PutMapping(path = "", produces = "application/json", consumes = "application/json")
	public ResponseEntity<Dashboard> updateDashboard(@RequestBody Dashboard dashboard) {
		try {
				if (dashboard.getId() != null && dashboardRepository.existsById(dashboard.getId())) {
					dashboardRepository.save(dashboard);
					return new ResponseEntity<>(dashboard, HttpStatus.OK);
				}else return ResponseEntity.notFound().build();
				
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
		try {
			dashboardRepository.deleteById(id);
			
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
