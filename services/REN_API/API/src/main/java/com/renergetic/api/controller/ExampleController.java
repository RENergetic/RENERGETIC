package com.renergetic.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.api.dao.ExampleRequest;
import com.renergetic.api.dao.ExampleResponse;
import com.renergetic.api.service.ExampleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Example Controller", description = "Manage example objects")
@RequestMapping("/api/example")
public class ExampleController {
	
	@Autowired
	private ExampleService exampleService;
	
	@GetMapping("test")
	public ResponseEntity<Void> test() {
		return ResponseEntity.ok(null);
	}
	
	@Operation(summary = "Get all examples saved on the repository")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<ExampleResponse>> getAllUsers(@RequestParam(required = false) Optional<Integer> offset,
                                                                @RequestParam(required = false) Optional<Integer> limit) {
		return ResponseEntity.ok(exampleService.get(offset.orElse(0), limit.orElse(1000)));
    }

	// Endpoint to save ExampleRequest to the repository
	@Operation(summary = "Save an example")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@PostMapping(path = "", produces = "application/json")
	public ResponseEntity<ExampleResponse> saveExample(@RequestBody ExampleRequest example) {
		return ResponseEntity.ok(exampleService.create(example));
	}

	@Operation(summary = "Update an example")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@PutMapping(path = "{id}", produces = "application/json")
	public ResponseEntity<ExampleResponse> updateExample(
		@PathVariable("id") Long id,
		@RequestBody ExampleRequest example) {
		return ResponseEntity.ok(exampleService.update(id, example));
	}

	@Operation(summary = "Delete an example")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@DeleteMapping(path = "", produces = "application/json")
	public ResponseEntity<Void> deleteExample(@RequestParam Long id) {
		exampleService.delete(id);
		return ResponseEntity.ok().build();
	}
}
