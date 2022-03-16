package com.inetum.app.controller;

import java.util.List;

import org.influxdb.dto.QueryResult;
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
import com.inetum.app.model.HeatSupply;
import com.inetum.app.repository.GeneralRepository;
import com.inetum.app.repository.HeatSupplyRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/general")
public class GeneralController {
	
	@Autowired
	GeneralRepository repository;
	
	@GetMapping("/query/{query}")
	public ResponseEntity<List<QueryResult.Result>> execQuery(@PathVariable String query){
		List<QueryResult.Result> ret = repository.query(query);
		
		if (ret != null)
			return ResponseEntity.ok(ret);
		else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
