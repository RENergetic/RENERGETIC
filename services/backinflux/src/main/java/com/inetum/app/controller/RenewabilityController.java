package com.inetum.app.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inetum.app.model.InfluxFunction;
import com.inetum.app.model.Renewability;
import com.inetum.app.repository.RenewabilityRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/renewability")
public class RenewabilityController {

	@Autowired
	RenewabilityRepository repository;
	
	@GetMapping()
	public ResponseEntity<List<Renewability>> getAllRenewability(@RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to,
			@RequestParam Map<String, String> tags){
		List<Renewability> ret;
		tags.remove("from");
		tags.remove("to");
		
		if (!from.isPresent() && !to.isPresent())
			ret = repository.select(tags);
		else
			ret = repository.select(from.orElse(""), to.orElse(""), tags);
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/{function}")
	public ResponseEntity<List<Renewability>> getProcessedConsumption(
			@PathVariable String function, 
			@RequestParam("from") Optional<String> from, 
			@RequestParam("to") Optional<String> to, 
			@RequestParam("group") Optional<String> group,
			@RequestParam Map<String, String> tags
			){
		tags.remove("from");
		tags.remove("to");
		tags.remove("group");
		
		List<Renewability> ret = repository.operate(InfluxFunction.obtain(function), from.orElse(""), to.orElse(""), group.orElse(""), tags);
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/")
	public ResponseEntity<?> addMeasurement(@RequestBody Renewability renewability, @RequestParam Optional<Map<String, String>> tags){
		repository.insert(renewability, tags.orElse(null));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
