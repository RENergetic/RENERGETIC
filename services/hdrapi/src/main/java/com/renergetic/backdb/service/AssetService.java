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
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.details.AssetDetails;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.MeasurementRepository;
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
	AssetDetailsRepository assetDetailsRepository;
	@Autowired
	MeasurementDetailsRepository measurementDetailsRepository;

	// ASSET CRUD OPERATIONS
	public AssetDAOResponse save(AssetDAORequest asset) {
		asset.setId(null);
		if (Asset.ALLOWED_TYPES.keySet().stream().anyMatch(asset.getType()::equalsIgnoreCase)) {
			return AssetDAOResponse.create(assetRepository.save(asset.mapToEntity()), null, null);
		}
		else throw new InvalidArgumentException("The asset type isn't allowed");
	}
	
	public boolean deleteById(Long id) {
		if (id != null && assetRepository.existsById(id)) {
			assetRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("The asset to delete doesn't exists");
	}

	public AssetDAOResponse update(AssetDAORequest asset, Long id) {
		if ( assetRepository.existsById(id) && 
				Asset.ALLOWED_TYPES.keySet().stream().anyMatch(asset.getType()::equalsIgnoreCase)) {
			asset.setId(id);
			return AssetDAOResponse.create(assetRepository.save(asset.mapToEntity()), null, null);
		} else throw new InvalidNonExistingIdException("The asset to update doesn't exists");
	}

	public AssetDAOResponse connect(Long id, Long connectId) {
		if ( assetRepository.existsById(id) && assetRepository.existsById(connectId)) {
			Asset asset = assetRepository.findById(id).get();
			asset.getAssets().add(assetRepository.findById(connectId).get());
			return AssetDAOResponse.create(assetRepository.save(asset), null, null);
		} else throw new InvalidNonExistingIdException("The assets to connect don't exists");
	}

	public MeasurementDAOResponse addMeasurement(Long assetid, Long measurementId) {
		if ( assetRepository.existsById(assetid) && measurementRepository.existsById(measurementId)) {
			Measurement measurement = measurementRepository.findById(measurementId).get();
			Asset asset = assetRepository.findById(assetid).get();
			
			boolean haveInfrastructure = false;
			for (Asset obj : measurement.getAssets()) {
				if (Asset.ALLOWED_TYPES.get(obj.getType()).equalsIgnoreCase("Infrastructure")) {
					haveInfrastructure = true;
					break;
				}
			}
			if (!(Asset.ALLOWED_TYPES.get(asset.getType()).equalsIgnoreCase("Infrastructure") && haveInfrastructure)) {
				measurement.getAssets().add(asset);
				return MeasurementDAOResponse.create(measurementRepository.save(measurement), null);
			}else throw new InvalidArgumentException("Measurement already have a Infrastructure connected");
			
		} else throw new InvalidNonExistingIdException(assetRepository.existsById(assetid) ? "The measurement doesn't exists" : "The asset doesn't exists");
	}

	public List<AssetDAOResponse> get(Map<String, String> filters, long offset, int limit) {
		Stream<Asset> stream = assetRepository.findAll(new OffSetPaging(offset, limit)).stream();
		
		if (filters != null)
			stream.filter(asset -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = asset.getName().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("type"))
					equals = asset.getType().equalsIgnoreCase(filters.get("type"));
				if (equals && filters.containsKey("location"))
					equals = asset.getLocation().equalsIgnoreCase(filters.get("location"));
				if (equals && filters.containsKey("owner"))
					equals = String.valueOf(asset.getOwner().getId()).equalsIgnoreCase(filters.get("owner"));
				if (equals && filters.containsKey("parent"))
					equals = String.valueOf(asset.getParentAsset().getId()).equalsIgnoreCase(filters.get("parent_id"));
				
				return equals;
			});
		List<AssetDAOResponse> assets = stream
				.map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAssets(asset)))
				.collect(Collectors.toList());
		
		if (assets.size() > 0)
			return assets;
		else throw new NotFoundException("No assets are found");
	}

	public AssetDAOResponse getById(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		if (asset != null)
			return AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAssets(asset));
		else throw new NotFoundException("No asset found related with id " + id);
	}

	public List<AssetDAOResponse> getConnectedTo(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		if (asset == null)
			throw new InvalidNonExistingIdException("No asset found related with id" + id);
		else if (asset.getAssets() != null && asset.getAssets().size() > 0)
			return asset.getAssets().stream()
				.map(obj -> AssetDAOResponse.create(obj, assetRepository.findByParentAsset(obj), measurementRepository.findByAssets(obj)))
				.collect(Collectors.toList());
		else throw new NotFoundException("No asset found connected with asset " + id);
	}

	public List<MeasurementDAOResponse> getMeasurements(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		if (asset == null)
			throw new InvalidNonExistingIdException("No asset found related with id" + id);
		else {
			List<Measurement> measurements = measurementRepository.findByAssets(asset);
			
			if (measurements != null && measurements.size() > 0)
				return measurements.stream()
							.map(obj -> MeasurementDAOResponse.create(obj, measurementDetailsRepository.findByMeasurementId(obj.getId())))
							.collect(Collectors.toList());
			else throw new NotFoundException("Asset " + id + " hasn't related measurements");
		}
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
