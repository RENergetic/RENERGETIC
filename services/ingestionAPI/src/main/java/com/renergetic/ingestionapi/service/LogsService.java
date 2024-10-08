package com.renergetic.ingestionapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.common.model.Request;
import com.renergetic.common.model.RequestError;
import com.renergetic.common.repository.RequestErrorRepository;
import com.renergetic.common.repository.RequestRepository;

@Service
public class LogsService {
	@Autowired
	RequestRepository repository;
	
	@Autowired
	RequestErrorRepository errorRepository;
	
	public Request save(Request request) {
		return repository.save(request);
	}

	public RequestError save(RequestError error) {
		return errorRepository.save(error);
	}

	public List<RequestError> save(List<RequestError> errors) {
		return errorRepository.saveAll(errors);
	}
}
