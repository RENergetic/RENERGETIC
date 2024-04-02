package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.renergetic.common.dao.InformationTileMeasurementDAORequest;
//import com.renergetic.common.dao.InformationTileMeasurementDAOResponse;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.InformationTileMeasurement; 
import com.renergetic.common.repository.InformationTileMeasurementRepository;
import com.renergetic.common.repository.UuidRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class InformationTileMeasurementService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	InformationTileMeasurementRepository tileRepository;

	@Autowired
	UuidRepository uuidRepository;

	// AREA CRUD OPERATIONS
//	public InformationTileMeasurementDAOResponse save(InformationTileMeasurementDAORequest tile) {
//		if (tile.getId() != null && tileRepository.existsById(tile.getId()))
//			throw new InvalidCreationIdAlreadyDefinedException("Information tile measurement with id " + tile.getId() + " already exists");
//
//		return InformationTileMeasurementDAOResponse.create(tileRepository.save(tile.mapToEntity()));
//	}
	
//	public boolean deleteById(Long id) {
//		if (id != null && tileRepository.existsById(id)) {
//			tileRepository.deleteById(id);
//			return true;
//		} else throw new InvalidNonExistingIdException("The tile to delete doesn't exists");
//	}

//	public InformationTileMeasurementDAOResponse update(InformationTileMeasurementDAORequest tile, Long id) {
//		if (id != null && tileRepository.existsById(id)) {
//			tile.setId(id);
//			return InformationTileMeasurementDAOResponse.create(tileRepository.save(tile.mapToEntity()));
//		} else throw new InvalidNonExistingIdException("The tile to update doesn't exists");
//	}

//	public List<InformationTileMeasurementDAOResponse> get(Map<String, String> filters, long offset, int limit) {
//		Stream<InformationTileMeasurement> stream = tileRepository.findAll(new OffSetPaging(offset, limit)).stream();
//		List<InformationTileMeasurementDAOResponse> tiles;
//
//		if (filters != null)
//			tiles = stream.filter(tile -> {
//				boolean equals = true;
//
////				if (filters.containsKey("asset") && tile.getAsset() != null)
////					equals = String.valueOf(tile.getAsset().getId()).equals(filters.get("asset"));
//				if (equals && filters.containsKey("measurement") && tile.getMeasurement() != null)
//					equals = String.valueOf(tile.getMeasurement().getId()).equals(filters.get("measurement"));
//
//				return equals;
//			}).map(area -> InformationTileMeasurementDAOResponse.create(area))
//					.collect(Collectors.toList());
//		else
//			tiles = stream
//				.map(area -> InformationTileMeasurementDAOResponse.create(area))
//				.collect(Collectors.toList());
//
//		if (tiles.size() > 0)
//			return tiles;
//		else throw new NotFoundException("No tiles found");
//	}
//
//	public InformationTileMeasurementDAOResponse getById(Long id) {
//		InformationTileMeasurement tile = tileRepository.findById(id).orElse(null);
//
//		if (tile != null)
//			return InformationTileMeasurementDAOResponse.create(tile);
//		else throw new NotFoundException("No tile found related with id " + id);
//	}
}
