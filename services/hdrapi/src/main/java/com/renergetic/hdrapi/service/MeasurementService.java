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
import com.renergetic.hdrapi.model.Direction;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.model.MeasurementType;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.model.Unit;
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
		measurement.setId(null);
		Measurement measurementEntity = measurement.mapToEntity();
		measurementEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
		return MeasurementDAOResponse.create(measurementRepository.save(measurementEntity), null);
	}
	
	public boolean deleteById(Long id) {
		if (id != null && measurementRepository.existsById(id)) {
			measurementRepository.deleteById(id);
			return true;
		} else return false;
	}

	public MeasurementDAOResponse update(MeasurementDAORequest measurement, Long id) {
		if ( measurementRepository.existsById(id) ) {
			measurement.setId(id);
			return MeasurementDAOResponse.create(measurementRepository.save(measurement.mapToEntity()), null);
		} else return null;
	}

	public List<MeasurementDAOResponse> get(Map<String, String> filters, long offset, int limit) {
		Page<Measurement> measurements = measurementRepository.findAll(new OffSetPaging(offset, limit));
		Stream<Measurement> stream = measurements.stream();
		
		if (filters != null)
			stream.filter(measurement -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = measurement.getName().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("type"))
					equals = measurement.getType().getName().equalsIgnoreCase(filters.get("type"));
//				if (equals && filters.containsKey("icon"))
//					equals = measurement.getIcon().equalsIgnoreCase(filters.get("icon"));
				if (equals && filters.containsKey("direction"))
					equals = measurement.getDirection().equals(Direction.valueOf(filters.get("direction")));
				if (equals && filters.containsKey("asset_id") && measurement.getAsset() != null) {
					equals = String.valueOf(measurement.getAsset().getId()).equalsIgnoreCase(filters.get("asset_id"));
				}
				
				return equals;
			});
		return stream
				.map(measurement -> MeasurementDAOResponse.create(measurement, measurementDetailsRepository.findByMeasurementId(measurement.getId())))
				.collect(Collectors.toList());
	}

	public Measurement getById(Long id) {
		return measurementRepository.findById(id).orElse(null);
	}
	
	// MEASUREMENTTYPE CRUD OPERATIONS
	public MeasurementType saveType(MeasurementType type) {
		//type.setId(null);
		return measurementTypeRepository.save(type);
	}
	
	public MeasurementType updateType(MeasurementType detail, Long id) {
		if ( measurementTypeRepository.existsById(id)) {
			detail.setId(id);
			return measurementTypeRepository.save(detail);
		} else return null;
	}

	public boolean deleteTypeById(Long id) {
		if (id != null && measurementTypeRepository.existsById(id)) {
			measurementTypeRepository.deleteById(id);
			return true;
		} else return false;
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
					equals = type.getUnit().equals(Unit.valueOf(filters.get("unit")));
				
				return equals;
			});
		return stream.collect(Collectors.toList());
	}

	public MeasurementType getTypeById(Long id) {
		return measurementTypeRepository.findById(id).orElse(null);
	}
	
	// MEASUREMENTTAGS CRUD OPERATIONS
	public MeasurementTags saveTag(MeasurementTags tag) {
		tag.setId(null);
		return measurementTagsRepository.save(tag);
	}
	
	public MeasurementTags updateTag(MeasurementTags tag, Long id) {
		if ( measurementTagsRepository.existsById(id)) {
			tag.setId(id);
			return measurementTagsRepository.save(tag);
		} else return null;
	}

	public boolean deleteTagById(Long id) {
		if (id != null && measurementTagsRepository.existsById(id)) {
			measurementTagsRepository.deleteById(id);
			return true;
		} else return false;
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
		return stream.collect(Collectors.toList());
	}

	public MeasurementTags getTagById(Long id) {
		return measurementTagsRepository.findById(id).orElse(null);
	}
	
	// MEASUREMENTDETAILS CRUD OPERATIONS
	public MeasurementDetails saveDetail(MeasurementDetails detail) {
		detail.setId(null);
		return measurementDetailsRepository.save(detail);
	}
	
	public MeasurementDetails updateDetail(MeasurementDetails detail, Long id) {
		if ( measurementDetailsRepository.existsById(id)) {
			detail.setId(id);
			return measurementDetailsRepository.save(detail);
		} else return null;
	}

	public boolean deleteDetailById(Long id) {
		if (id != null && measurementDetailsRepository.existsById(id)) {
			measurementDetailsRepository.deleteById(id);
			return true;
		} else return false;
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
		return stream.collect(Collectors.toList());
	}

	public MeasurementDetails getDetailById(Long id) {
		return measurementDetailsRepository.findById(id).orElse(null);
	}

	public List<MeasurementDetails> getDetailsByMeasurementId(Long id) {
		return measurementDetailsRepository.findByMeasurementId(id);
	}
}
