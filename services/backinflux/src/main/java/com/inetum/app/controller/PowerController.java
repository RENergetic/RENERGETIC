package com.inetum.app.controller;

import java.util.List;
import java.util.Optional;

import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inetum.app.model.InfluxFunction;
import com.inetum.app.model.Power;
import com.inetum.app.repository.PowerRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/power")
public class PowerController {

	@Autowired
	PowerRepository repository;
	
	@GetMapping()
	public ResponseEntity<List<Power>> getAllConsumption(@RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to){
		List<Power> ret;
		if (!from.isPresent() && !to.isPresent())
			ret = repository.select();
		else
			ret = repository.select(from.orElse(""), to.orElse(""));
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/{function}")
	public ResponseEntity<List<Power>> getProcessedConsumption(@PathVariable String function, @RequestParam("from") Optional<String> from, @RequestParam("to") Optional<String> to, @RequestParam("group") Optional<String> group){
		List<Power> ret = repository.operate(InfluxFunction.obtain(function), from.orElse(""), to.orElse(""), group.orElse(""));
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/query/{query}")
	public ResponseEntity<List<QueryResult.Result>> execQuery(@PathVariable String query){
		List<QueryResult.Result> ret = repository.query(query);
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
