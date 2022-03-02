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
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.details.AssetDetails;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.MeasurementRepository;
import com.renergetic.backdb.repository.information.AssetDetailsRepository;

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

	// ASSET CRUD OPERATIONS
	public AssetDAOResponse save(AssetDAORequest asset) {
		asset.setId(null);
		if (Asset.ALLOWED_TYPES.keySet().stream().anyMatch(asset.getType()::equalsIgnoreCase)) {
			return AssetDAOResponse.create(assetRepository.save(asset.mapToEntity()), null, null);
		}
		else return null;
	}
	
	public boolean deleteById(Long id) {
		if (id != null && assetRepository.existsById(id)) {
			assetRepository.deleteById(id);
			return true;
		} else return false;
	}

	public AssetDAOResponse update(AssetDAORequest asset, Long id) {
		if ( assetRepository.existsById(id) && 
				Asset.ALLOWED_TYPES.keySet().stream().anyMatch(asset.getType()::equalsIgnoreCase)) {
			asset.setId(id);
			return AssetDAOResponse.create(assetRepository.save(asset.mapToEntity()), null, null);
		} else return null;
	}

	public AssetDAOResponse connect(Long id, Long connectId) {
		if ( assetRepository.existsById(id) && assetRepository.existsById(connectId)) {
			Asset asset = assetRepository.findById(id).get();
			asset.getAssets().add(assetRepository.findById(connectId).get());
			return AssetDAOResponse.create(assetRepository.save(asset), null, null);
		} else return null;
	}

	public List<AssetDAOResponse> get(Map<String, String> filters) {
		List<Asset> assets = assetRepository.findAll();
		Stream<Asset> stream = assets.stream();
		
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
		return stream
				.map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAssets(asset)))
				.collect(Collectors.toList());
	}

	public AssetDAOResponse getById(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		
		return AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAssets(asset));
	}

	public List<AssetDAOResponse> getConnectedTo(Long id) {
		Asset asset = assetRepository.findById(id).orElse(null);
		return asset != null
				? asset.getAssets().stream()
						.map(obj -> AssetDAOResponse.create(obj, assetRepository.findByParentAsset(obj), measurementRepository.findByAssets(obj)))
						.collect(Collectors.toList())
				: null;
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
		} else return false;
	}
	
	public List<AssetDetails> getDetails(Long asset_id, Map<String, String> filters) {
		List<AssetDetails> details = assetDetailsRepository.findAll();
		
		if  (filters != null) {
			if (filters.containsKey("name"))
				details = details.stream().filter(detail -> detail.getKey().equalsIgnoreCase(filters.get("key"))).collect(Collectors.toList());
		}
		return details;
	}

	public AssetDetails getDetailById(Long id) {
		return assetDetailsRepository.findById(id).orElse(null);
	}

	public List<AssetDetails> getDetailsByAssetId(Long id) {
		return assetDetailsRepository.findByAssetId(id);
	}
}
