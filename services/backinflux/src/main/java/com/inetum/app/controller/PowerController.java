package com.inetum.app.controller;

import java.util.List;
import java.util.Optional;

import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inetum.app.model.Power;
import com.inetum.app.repository.PowerRepository;

@RestController
@RequestMapping("/api/power")
public class PowerController {

	@Autowired
	PowerRepository repository;
	
	@GetMapping()
	public ResponseEntity<List<Power>> getAllConsumption(@RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to){
		if (!from.isPresent() && !to.isPresent())
			return ResponseEntity.ok(repository.select());
		else return ResponseEntity.ok(repository.select(from.orElse(""), to.orElse("")));
	}
	
	@GetMapping("/sum")
	public ResponseEntity<List<Power>> getSumConsumption(@RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to, @RequestParam("group") Optional<String> group){
		return ResponseEntity.ok(repository.sum(from.orElse(""), to.orElse(""), group.orElse("")));
	}
	
	@GetMapping("/query/{query}")
	public ResponseEntity<List<QueryResult.Result>> execQuery(@PathVariable String query){
		return ResponseEntity.ok(repository.query(query));
	}
}
