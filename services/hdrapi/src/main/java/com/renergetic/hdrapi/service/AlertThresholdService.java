//package com.renergetic.hdrapi.service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.renergetic.common.dao.AlertThresholdDAORequest;
//import com.renergetic.common.dao.AlertThresholdDAOResponse;
//import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
//import com.renergetic.common.exception.InvalidNonExistingIdException;
//import com.renergetic.common.exception.NotFoundException;
//import com.renergetic.common.model.AlertThreshold;
//import com.renergetic.common.repository.AlertThresholdRepository;
//import com.renergetic.hdrapi.service.utils.OffSetPaging;
//
//@Service
//public class AlertThresholdService {
//	@PersistenceContext
//	EntityManager entityManager;
//
//	@Autowired
//	AlertThresholdRepository alertThresholdRepository;
//
//	// ALERTTHRESHOLD CRUD OPERATIONS
//	public AlertThresholdDAOResponse save(AlertThresholdDAORequest alertThreshold) {
//		if(alertThreshold.getId() !=  null && alertThresholdRepository.existsById(alertThreshold.getId()))
//	    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a alert threshold with ID " + alertThreshold.getId());
//
//		AlertThreshold alertThresholdEntity = alertThreshold.mapToEntity();
//		return AlertThresholdDAOResponse.create(alertThresholdRepository.save(alertThresholdEntity));
//	}
//
//	public boolean deleteById(Long id) {
//		if (id != null && alertThresholdRepository.existsById(id)) {
//			alertThresholdRepository.deleteById(id);
//			return true;
//		} else throw new InvalidNonExistingIdException("The alert threshold to delete doesn't exists");
//	}
//
//	public AlertThresholdDAOResponse update(AlertThresholdDAORequest alertThreshold, Long id) {
//		if (alertThresholdRepository.existsById(id)) {
//			alertThreshold.setId(id);
//			AlertThreshold alertThresholdEntity = alertThreshold.mapToEntity();
//
//			return AlertThresholdDAOResponse.create(alertThresholdRepository.save(alertThresholdEntity));
//		} else throw new InvalidNonExistingIdException("The alert threshold to update doesn't exists");
//	}
//
//	public List<AlertThresholdDAOResponse> get(Map<String, String> filters, long offset, int limit) {
//		Stream<AlertThreshold> stream = alertThresholdRepository.findAll(new OffSetPaging(offset, limit)).stream();
//		List<AlertThresholdDAOResponse> alertThresholds;
//
//		if (filters != null)
//			alertThresholds = stream.filter(alertThreshold -> {
//				boolean equals = true;
//
//				if (filters.containsKey("threshold_type"))
//					equals = alertThreshold.getThresholdType().name().equalsIgnoreCase(filters.get("threshold_type"));
//				if (filters.containsKey("threshold_constraint"))
//					equals = alertThreshold.getThresholdConstraint().equalsIgnoreCase(filters.get("threshold_constraint"));
//				if (filters.containsKey("aggregation_type") && alertThreshold.getAggregationType() != null)
//					equals = alertThreshold.getAggregationType().name().equalsIgnoreCase(filters.get("aggregation_type"));
//				if (equals && filters.containsKey("interval"))
//					equals = String.valueOf(alertThreshold.getAggregationInterval()).equals(filters.get("interval"));
//
//				return equals;
//			}).map(alertThreshold -> AlertThresholdDAOResponse.create(alertThreshold))
//					.collect(Collectors.toList());
//		else
//			alertThresholds = stream
//				.map(alertThreshold -> AlertThresholdDAOResponse.create(alertThreshold))
//				.collect(Collectors.toList());
//
//		if (alertThresholds.size() > 0)
//			return alertThresholds;
//		else throw new NotFoundException("No alert thresholds found");
//	}
//
//	public AlertThresholdDAOResponse getById(Long id) {
//		AlertThreshold alertThreshold = alertThresholdRepository.findById(id).orElse(null);
//
//		if (alertThreshold != null)
//			return AlertThresholdDAOResponse.create(alertThreshold);
//		else throw new NotFoundException("No alert threshold found related with id " + id);
//	}
//}
