package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.MeasurementDAORequest;
import com.renergetic.hdrapi.dao.MeasurementDAOResponse;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Direction;
import com.renergetic.hdrapi.model.Domain;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.MeasurementType;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.model.details.MeasurementDetails;
import com.renergetic.hdrapi.model.details.MeasurementTags;
import com.renergetic.hdrapi.repository.MeasurementRepository;
import com.renergetic.hdrapi.repository.MeasurementTagsRepository;
import com.renergetic.hdrapi.repository.MeasurementTypeRepository;
import com.renergetic.hdrapi.repository.UuidRepository;
import com.renergetic.hdrapi.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class MeasurementService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	MeasurementRepository measurementRepository;
	@Autowired
	MeasurementTypeRepository measurementTypeRepository;
	@Autowired
	MeasurementDetailsRepository measurementDetailsRepository;
	@Autowired
	MeasurementTagsRepository measurementTagsRepository;
	@Autowired
	UuidRepository uuidRepository;

	// ASSET CRUD OPERATIONS
	public MeasurementDAOResponse save(MeasurementDAORequest measurement) {
		if(measurement.getId() !=  null && measurementRepository.existsById(measurement.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a measurement with ID " + measurement.getId());
		
		Measurement measurementEntity = measurement.mapToEntity();
		measurementEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
		return MeasurementDAOResponse.create(measurementRepository.save(measurementEntity), null);
	}
	
	public boolean deleteById(Long id) {
		if (id != null && measurementRepository.existsById(id)) {
			measurementRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No measurement with id " + id + " found");
	}

	public MeasurementDAOResponse update(MeasurementDAORequest measurement, Long id) {
		if ( id != null && measurementRepository.existsById(id) ) {
			measurement.setId(id);
			return MeasurementDAOResponse.create(measurementRepository.save(measurement.mapToEntity()), null);
		} else throw new InvalidNonExistingIdException("No measurement with id " + id + " found");
	}

	public List<MeasurementDAOResponse> get(Map<String, String> filters, long offset, int limit) {
		Page<Measurement> measurements = measurementRepository.findAll(new OffSetPaging(offset, limit));
		Stream<Measurement> stream = measurements.stream();
		List<MeasurementDAOResponse> list;
		
		if (filters != null)
			list = stream.filter(measurement -> {
				boolean equals = true;
				
				if (filters.containsKey("name")) {
					equals = measurement.getName().toLowerCase().contains(filters.get("name").toLowerCase());
				} if (equals && filters.containsKey("type"))
					equals = measurement.getType().getName().equalsIgnoreCase(filters.get("type"));
				if (equals && filters.containsKey("direction"))
					equals = measurement.getDirection().equals(Direction.valueOf(filters.get("direction")));
				if (equals && filters.containsKey("domain") && measurement.getAsset() != null) {
					equals = measurement.getDomain().equals(Domain.valueOf(filters.get("domain")));
				}
				
				return equals;
			}).map(measurement -> MeasurementDAOResponse.create(measurement, measurementDetailsRepository.findByMeasurementId(measurement.getId())))
			.collect(Collectors.toList());
		else
			list = stream
				.map(measurement -> MeasurementDAOResponse.create(measurement, measurementDetailsRepository.findByMeasurementId(measurement.getId())))
				.collect(Collectors.toList());
		
		if (list != null && list.size() > 0)
			return list;
		else throw new NotFoundException("No measurements found");
	}

	public Measurement getById(Long id) {
		return measurementRepository.findById(id).orElseThrow(() -> new NotFoundException("No measurements with id " + id + " found"));
	}
	
	// MEASUREMENTTYPE CRUD OPERATIONS
	public MeasurementType saveType(MeasurementType type) {
    	if (type.getId() != null && measurementTypeRepository.existsById(type.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a measurement type with ID " + type.getId());
    	
		return measurementTypeRepository.save(type);
	}
	
	public MeasurementType updateType(MeasurementType detail, Long id) {
		if ( id != null && measurementTypeRepository.existsById(id)) {
			detail.setId(id);
			return measurementTypeRepository.save(detail);
		} else throw new InvalidNonExistingIdException("No measurement type with id " + id + "found");
	}

	public boolean deleteTypeById(Long id) {
		if (id != null && measurementTypeRepository.existsById(id)) {
			measurementTypeRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No measurement type with id " + id + "found");
	}
	
	public List<MeasurementType> getTypes(Map<String, String> filters, long offset, int limit) {
		Page<MeasurementType> types = measurementTypeRepository.findAll(new OffSetPaging(offset, limit));
		Stream<MeasurementType> stream = types.stream();
		
		if (filters != null)
			stream.filter(type -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = type.getName().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("unit"))
					equals = type.getUnit().equals(filters.get("unit"));
				
				return equals;
			});
		List<MeasurementType> list = stream.collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No measurement types found");		
	}

	public MeasurementType getTypeById(Long id) {
		return measurementTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("No measurement types with id " + id + " found"));
	}
	
	// MEASUREMENTTAGS CRUD OPERATIONS
	public MeasurementTags saveTag(MeasurementTags tag) {
		if(tag.getId() !=  null && measurementTagsRepository.existsById(tag.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a tag with ID " + tag.getId());
		
		return measurementTagsRepository.save(tag);
	}
	
	public MeasurementTags updateTag(MeasurementTags tag, Long id) {
		if ( id != null && measurementTagsRepository.existsById(id)) {
			tag.setId(id);
			return measurementTagsRepository.save(tag);
		} else throw new InvalidNonExistingIdException("No tag with id " + id + "found");
	}

	public boolean deleteTagById(Long id) {
		if (id != null && measurementTagsRepository.existsById(id)) {
			measurementTagsRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No tag with id " + id + "found");
	}
	
	public List<MeasurementTags> getTags(Map<String, String> filters, long offset, int limit) {
		Page<MeasurementTags> tags = measurementTagsRepository.findAll(new OffSetPaging(offset, limit));
		Stream<MeasurementTags> stream = tags.stream();
		
		if (filters != null)
			stream.filter(Detail -> {
				boolean equals = true;
				
				if (filters.containsKey("key"))
					equals = Detail.getKey().equalsIgnoreCase(filters.get("key"));
				
				return equals;
			});
		List<MeasurementTags> list = stream.collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No tags found");
	}

	public MeasurementTags getTagById(Long id) {
		return measurementTagsRepository.findById(id).orElseThrow(() -> new NotFoundException("No tags with id " + id + " found"));
	}
	
	// MEASUREMENTDETAILS CRUD OPERATIONS
	public MeasurementDetails saveDetail(MeasurementDetails detail) {
		if(detail.getId() !=  null && measurementDetailsRepository.existsById(detail.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a detail with ID " + detail.getId());
		
		return measurementDetailsRepository.save(detail);
	}
	
	public MeasurementDetails updateDetail(MeasurementDetails detail, Long id) {
		if ( id != null && measurementDetailsRepository.existsById(id)) {
			detail.setId(id);
			return measurementDetailsRepository.save(detail);
		} else throw new InvalidNonExistingIdException("No measurement details with id " + id + "found");
	}

	public boolean deleteDetailById(Long id) {
		if (id != null && measurementDetailsRepository.existsById(id)) {
			measurementDetailsRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No measurement details with id " + id + "found");
	}
	
	public List<MeasurementDetails> getDetails(Map<String, String> filters, long offset, int limit) {
		Page<MeasurementDetails> details = measurementDetailsRepository.findAll(new OffSetPaging(offset, limit));
		Stream<MeasurementDetails> stream = details.stream();
		
		if (filters != null)
			stream.filter(Detail -> {
				boolean equals = true;
				
				if (filters.containsKey("key"))
					equals = Detail.getKey().equalsIgnoreCase(filters.get("key"));
				
				return equals;
			});
		List<MeasurementDetails> list = stream.collect(Collectors.toList());

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No tags found");
	}

	public MeasurementDetails getDetailById(Long id) {
		return measurementDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException("No measurement details with id " + id + " found"));
	}

	public List<MeasurementDetails> getDetailsByMeasurementId(Long id) {
		List<MeasurementDetails> list = measurementDetailsRepository.findByMeasurementId(id);

    	if (list != null && list.size() > 0)
    		return list;
		else throw new NotFoundException("No tags found");
	}
}
