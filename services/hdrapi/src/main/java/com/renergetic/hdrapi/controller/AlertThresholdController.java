//package com.renergetic.hdrapi.controller;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.renergetic.common.dao.AlertThresholdDAORequest;
//import com.renergetic.common.dao.AlertThresholdDAOResponse;
//import com.renergetic.hdrapi.service.AlertThresholdService;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
//@CrossOrigin(origins = "*")
//@RestController
//@Tag(name = "AlertThreshold Controller", description = "Allows add and see AlertThreshold")
//@RequestMapping("/api/threshold")
//public class AlertThresholdController {
//
//	@Autowired
//	AlertThresholdService alertSv;
//
////=== GET REQUESTS ====================================================================================
//
//	@Operation(summary = "Get All AlertThreshold")
//	@ApiResponse(responseCode = "200", description = "Request executed correctly")
//	@GetMapping(path = "", produces = "application/json")
//	public ResponseEntity<List<AlertThresholdDAOResponse>> getAllAlertThreshold (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
//		List<AlertThresholdDAOResponse> alert = new ArrayList<>();
//
//		alert = alertSv.get(null, offset.orElse(0L), limit.orElse(20));
//
//		return new ResponseEntity<>(alert, HttpStatus.OK);
//	}
//
//	@Operation(summary = "Get AlertThreshold by id")
//	@ApiResponses({
//		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
//		@ApiResponse(responseCode = "400", description = "Malformed URL"),
//		@ApiResponse(responseCode = "404", description = "No alert found with this id")
//	})
//	@GetMapping(path = "{id}", produces = "application/json")
//	public ResponseEntity<AlertThresholdDAOResponse> getAlertThresholdById (@PathVariable Long id){
//		AlertThresholdDAOResponse alert = null;
//
//		alert = alertSv.getById(id);
//
//		return new ResponseEntity<>(alert, alert != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
//	}
//
////=== POST REQUESTS ===================================================================================
//
//	@Operation(summary = "Create a new AlertThreshold")
//	@ApiResponses({
//			@ApiResponse(responseCode = "201", description = "AlertThreshold saved correctly"),
//			@ApiResponse(responseCode = "500", description = "Error saving alert")
//		}
//	)
//	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
//	public ResponseEntity<AlertThresholdDAOResponse> createAlertThreshold(@RequestBody AlertThresholdDAORequest alert) {
//		AlertThresholdDAOResponse _alert = alertSv.save(alert);
//
//		return new ResponseEntity<>(_alert, HttpStatus.CREATED);
//	}
//
////=== PUT REQUESTS ====================================================================================
//
//	@Operation(summary = "Update a existing AlertThreshold")
//	@ApiResponses({
//			@ApiResponse(responseCode = "200", description = "AlertThreshold saved correctly"),
//			@ApiResponse(responseCode = "404", description = "AlertThreshold not exist"),
//			@ApiResponse(responseCode = "500", description = "Error saving alert")
//		}
//	)
//	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
//	public ResponseEntity<AlertThresholdDAOResponse> updateAlertThreshold(@RequestBody AlertThresholdDAORequest alert, @PathVariable Long id) {
//		alert.setId(id);
//		return new ResponseEntity<>(alertSv.update(alert, id), HttpStatus.OK);
//	}
//
////=== DELETE REQUESTS =================================================================================
//
//	@Operation(summary = "Delete a existing AlertThreshold")
//	@ApiResponses({
//			@ApiResponse(responseCode = "204", description = "AlertThreshold deleted correctly"),
//			@ApiResponse(responseCode = "500", description = "Error saving alert")
//		}
//	)
//	@DeleteMapping(path = "/{id}")
//	public ResponseEntity<?> deleteAlertThreshold(@PathVariable Long id) {
//		alertSv.deleteById(id);
//
//		return ResponseEntity.noContent().build();
//	}
//}
