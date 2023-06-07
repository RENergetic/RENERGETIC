package com.renergetic.kpiapi.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.dao.AbstractMeterDAO;
import com.renergetic.kpiapi.dao.AbstractMeterDataDAO;
import com.renergetic.kpiapi.exception.IdAlreadyDefinedException;
import com.renergetic.kpiapi.exception.IdNoDefinedException;
import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.repository.AbstractMeterRepository;

@Service
public class AbstractMeterDataService {
	
	@Autowired
	private AbstractMeterRepository amRepo;
	
	// Do a request to get data from an API using HtpAPIs class
	public List<AbstractMeterDataDAO> get(String name, Domain domain, String group) {
		
		return null;
	}
}
