package com.renergetic.backdb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.backdb.dao.AssetDAORequest;
import com.renergetic.backdb.dao.AssetDAOResponse;
import com.renergetic.backdb.dao.MeasurementDAOResponse;
import com.renergetic.backdb.exception.InvalidArgumentException;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.AssetCategory;
import com.renergetic.backdb.model.AssetType;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.UUID;
import com.renergetic.backdb.model.details.AssetDetails;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.AssetTypeRepository;
import com.renergetic.backdb.repository.MeasurementRepository;
import com.renergetic.backdb.repository.UuidRepository;
import com.renergetic.backdb.repository.information.AssetDetailsRepository;
import com.renergetic.backdb.repository.information.MeasurementDetailsRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;

@Service
public class AssetService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	AssetRepository assetRepository;
	@Autowired
	MeasurementRepository measurementRepository;
	@Autowired
	AssetTypeRepository assetTypeRepository;
	@Autowired
	AssetDetailsRepository assetDetailsRepository;
	@Autowired
	MeasurementDetailsRepository measurementDetailsRepository;
	@Autowired
	UuidRepository uuidRepository;

	// ASSET CRUD OPERATIONS
	public AssetDAOResponse save(AssetDAORequest asset) {
		asset.setId(null);
		if (assetTypeRepository.existsById(asset.getType())) {
			Asset assetEntity = asset.mapToEntity();
			assetEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
			System.err.println(assetEntity);
			return AssetDAOResponse.create(assetRepository.save(assetEntity), null, null);
		}
		else throw new InvalidArgumentException("The asset type doesn't exists");
	}
	
	public boolean deleteById(Long id) {
		if (id != null && assetRepository.existsById(id)) {
			assetRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("The asset to delete doesn't exists");
	}

	public AssetDAOResponse update(AssetDAORequest asset, Long id) {
		boolean assetExists = assetRepository.existsById(id);
		
		if ( assetExists && 
				assetTypeRepository.existsById(asset.getType())) {
			asset.setId(id);
			return AssetDAOResponse.create(assetRepository.save(asset.mapToEntity()), null, null);
		} else throw new InvalidNonExistingIdException(assetExists
				? "The asset type doesn't exists"
				: "The asset to update doesn't exists");
	}

	public AssetDAOResponse connect(Long id, Long connectId) {
		boolean assetExists = assetRepository.existsById(id);

		if ( assetExists && assetRepository.existsById(connectId)) {
			Asset asset = assetRepository.findById(id).get();
			asset.getAssets().add(assetRepository.findById(connectId).get());
			return AssetDAOResponse.create(assetRepository.save(asset), null, null);
		} else throw new InvalidNonExistingIdException("The assets to connect don't exists");
	}

	public MeasurementDAOResponse addMeasurement(Long assetid, Long measurementId) {
		if ( assetRepository.existsById(assetid) && measurementRepository.existsById(measurementId)) {
			Measurement measurement = measurementRepository.findById(measurementId).get();
			return MeasurementDAOResponse.create(measurementRepository.save(measurement), null);
			
		} else throw new InvalidNonExistingIdException(assetRepository.existsById(assetid) ? "The measurement doesn't exists" : "The asset doesn't exists");
	}

	public List<AssetDAOResponse> get(Map<String, String> filters, long offset, int limit) {
		Stream<Asset> stream = assetRepository.findAll(new OffSetPaging(offset, limit)).stream();
		List<AssetDAOResponse> assets;
		
		if (filters != null)
			assets = stream.filter(asset -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = asset.getName().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("type") && asset.getType() != null)
					equals = asset.getType().getName().equalsIgnoreCase(filters.get("type")) ||
							asset.getType().getLabel().equalsIgnoreCase(filters.get("type"));
				if (equals && filters.containsKey("category") && asset.getType() != null) {
					equals = asset.getType().getCategory().equals(AssetCategory.valueOf(filters.get("category")));
				}
				if (equals && filters.containsKey("location"))
					equals = asset.getLocation().equalsIgnoreCase(filters.get("location"));
				if (equals && filters.containsKey("owner"))
					equals = asset.getOwner() != null? String.valueOf(asset.getOwner().getId()).equalsIgnoreCase(filters.get("owner")) : false;
				if (equals && filters.containsKey("parent"))
					equals = asset.getParentAsset() != null? String.valueOf(asset.getParentAsset().getId()).equalsIgnoreCase(filters.get("parent")) : false;
				
				return equals;
			}).map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAsset(asset)))
					.collect(Collectors.toList());
		else
			assets = stream
				.map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAsset(asset)))
				.collect(Collectors.toList());
		
		if (assets.size() > 0)
			return assets;
		else throw new NotFoundException("No assets are found");
	}

	public AssetDAOResponse getById(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		if (asset != null)
			return AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAsset(asset));
		else throw new NotFoundException("No asset found related with id " + id);
	}

	public List<AssetDAOResponse> getConnectedTo(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		if (asset == null)
			throw new InvalidNonExistingIdException("No asset found related with id" + id);
		else if (asset.getAssets() != null && asset.getAssets().size() > 0)
			return asset.getAssets().stream()
				.map(obj -> AssetDAOResponse.create(obj, assetRepository.findByParentAsset(obj), measurementRepository.findByAsset(obj)))
				.collect(Collectors.toList());
		else throw new NotFoundException("No asset found connected with asset " + id);
	}

	public List<MeasurementDAOResponse> getMeasurements(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		if (asset == null)
			throw new InvalidNonExistingIdException("No asset found related with id" + id);
		else {
			List<Measurement> measurements = measurementRepository.findByAsset(asset);
			
			if (measurements != null && measurements.size() > 0)
				return measurements.stream()
							.map(obj -> MeasurementDAOResponse.create(obj, measurementDetailsRepository.findByMeasurementId(obj.getId())))
							.collect(Collectors.toList());
			else throw new NotFoundException("Asset " + id + " hasn't related measurements");
		}
	}
	
	// ASSETTYPE CRUD OPERATIONS
	public AssetType saveType(AssetType type) {
		//type.setId(null);
		return assetTypeRepository.save(type);
	}
	
	public AssetType updateType(AssetType detail, Long id) {
		if ( assetTypeRepository.existsById(id)) {
			detail.setId(id);
			return assetTypeRepository.save(detail);
		} else return null;
	}

	public boolean deleteTypeById(Long id) {
		if (id != null && assetTypeRepository.existsById(id)) {
			assetTypeRepository.deleteById(id);
			return true;
		} else return false;
	}
	
	public List<AssetType> getTypes(Map<String, String> filters, long offset, int limit) {
		Stream<AssetType> stream = assetTypeRepository.findAll(new OffSetPaging(offset, limit)).stream();
		
		if (filters != null)
			return stream.filter(type -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = type.getName().equalsIgnoreCase(filters.get("name")) ||
							type.getLabel().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("category"))
					equals = type.getCategory().equals(AssetCategory.valueOf(filters.get("category")));
				
				return equals;
			}).collect(Collectors.toList());
		else return stream.collect(Collectors.toList());
	}

	public AssetType getTypeById(Long id) {
		return assetTypeRepository.findById(id).orElse(null);
	}
	
	// ASSETDETAILS CRUD OPERATIONS
	public AssetDetails saveDetail(AssetDetails detail) {
		detail.setId(null);
		return assetDetailsRepository.save(detail);
	}
	
	public AssetDetails updateDetail(AssetDetails detail, Long id) {
		if ( assetDetailsRepository.existsById(id)) {
			detail.setId(id);
			return assetDetailsRepository.save(detail);
		} else return null;
	}

	public boolean deleteDetailById(Long id) {
		if (id != null && assetRepository.existsById(id)) {
			assetDetailsRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No asset detail with id" + id + "found");
	}
	
	public List<AssetDetails> getDetails(Map<String, String> filters, long offset, int limit) {
		Stream<AssetDetails> stream = assetDetailsRepository.findAll(new OffSetPaging(offset, limit)).stream();
		
		if (filters != null)
			stream.filter(Detail -> {
				boolean equals = true;
				
				if (filters.containsKey("key"))
					equals = Detail.getKey().equalsIgnoreCase(filters.get("key"));
				
				return equals;
			});
		List<AssetDetails> details = stream.collect(Collectors.toList());
		if (details != null && details.size() > 0)
			return details;
		else throw new NotFoundException("Not details found");
	}

	public AssetDetails getDetailById(Long id) {
		return assetDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException("No asset detais found related with id " + id));
	}

	public List<AssetDetails> getDetailsByAssetId(Long id) {
		List<AssetDetails> details = assetDetailsRepository.findByAssetId(id);
		
		if (details != null && details.size() > 0)
			return details;
		else throw new NotFoundException("No details related with asset " + id + "found");
	}
}
