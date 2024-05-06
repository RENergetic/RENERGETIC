// TODO: review why it should is commented 
//package com.renergetic.baseapi.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.renergetic.baseapi.service.InformationTileMeasurementService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;

// @CrossOrigin(origins = "*")
// @RestController
// @Tag(name = "InformationTileMeasurement Controller", description = "Allows add and see Area")
// @RequestMapping("/api/informationTileMeasurement")
// public class InformationTileMeasurementController {
	
// 	@Autowired
// 	InformationTileMeasurementService informationTileMeasurementSv;

// //=== GET REQUESTS ====================================================================================
			
// //	@Operation(summary = "Get All Information Tile Measurement")
// //	@ApiResponse(responseCode = "200", description = "Request executed correctly")
// //	@GetMapping(path = "", produces = "application/json")
// //	public ResponseEntity<List<InformationTileMeasurementDAOResponse>> getAllInformationTileMeasurement (@RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Integer> limit){
// //		List<InformationTileMeasurementDAOResponse> informationTileMeasurement = new ArrayList<>();
// //
// //		informationTileMeasurement = informationTileMeasurementSv.get(null, offset.orElse(0L), limit.orElse(20));
// //
// //		return new ResponseEntity<>(informationTileMeasurement, HttpStatus.OK);
// //	}
// //
// //	@Operation(summary = "Get Information Tile Measurement by id")
// //	@ApiResponses({
// //		@ApiResponse(responseCode = "200", description = "Request executed correctly"),
// //		@ApiResponse(responseCode = "400", description = "Malformed URL"),
// //		@ApiResponse(responseCode = "404", description = "No information tile measurement found with this id")
// //	})
// //	@GetMapping(path = "{id}", produces = "application/json")
// //	public ResponseEntity<InformationTileMeasurementDAOResponse> getInformationTileMeasurementById (@PathVariable Long id){
// //		InformationTileMeasurementDAOResponse informationTileMeasurement = null;
// //
// //		informationTileMeasurement = informationTileMeasurementSv.getById(id);
// //
// //		return new ResponseEntity<>(informationTileMeasurement, informationTileMeasurement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
// //	}

// //=== POST REQUESTS ===================================================================================
			
// //	@Operation(summary = "Create a new Information Tile Measurement")
// //	@ApiResponses({
// //			@ApiResponse(responseCode = "201", description = "Information tile measurement saved correctly"),
// //			@ApiResponse(responseCode = "500", description = "Error saving informationTileMeasurement")
// //		}
// //	)
// //	@PostMapping(path = "", produces = "application/json", consumes = "application/json")
// //	public ResponseEntity<InformationTileMeasurementDAOResponse> createInformationTileMeasurement(@RequestBody InformationTileMeasurementDAORequest informationTileMeasurement) {
// //		InformationTileMeasurementDAOResponse _informationTileMeasurement = informationTileMeasurementSv.save(informationTileMeasurement);
// //
// //		return new ResponseEntity<>(_informationTileMeasurement, HttpStatus.CREATED);
// //	}

// //=== PUT REQUESTS ====================================================================================
// //
// //	@Operation(summary = "Update a existing Information Tile Measurement")
// //	@ApiResponses({
// //			@ApiResponse(responseCode = "200", description = "Information Tile Measurement saved correctly"),
// //			@ApiResponse(responseCode = "404", description = "Information Tile Measurement not exist"),
// //			@ApiResponse(responseCode = "500", description = "Error saving information tile measurement")
// //		}
// //	)
// //	@PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
// //	public ResponseEntity<InformationTileMeasurementDAOResponse> updateInformationTileMeasurement(@RequestBody InformationTileMeasurementDAORequest informationTileMeasurement, @PathVariable Long id) {
// //		informationTileMeasurement.setId(id);
// //		return new ResponseEntity<>(informationTileMeasurementSv.update(informationTileMeasurement, id), HttpStatus.OK);
// //	}

// //=== DELETE REQUESTS =================================================================================
// //
// //	@Operation(summary = "Delete a existing InformationTileMeasurement")
// //	@ApiResponses({
// //			@ApiResponse(responseCode = "204", description = "Information Tile Measurement deleted correctly"),
// //			@ApiResponse(responseCode = "500", description = "Error saving information tile measurement")
// //		}
// //	)
// //	@DeleteMapping(path = "/{id}")
// //	public ResponseEntity<?> deleteInformationTileMeasurement(@PathVariable Long id) {
// //		informationTileMeasurementSv.deleteById(id);
// //
// //		return ResponseEntity.noContent().build();
// //	}
// }
