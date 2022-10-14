package com.renergetic.hdrapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.*;
import com.renergetic.hdrapi.exception.InvalidArgumentException;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.*;
import com.renergetic.hdrapi.model.details.AssetDetails;
import com.renergetic.hdrapi.repository.*;
import com.renergetic.hdrapi.repository.information.AssetDetailsRepository;
import com.renergetic.hdrapi.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	AssetConnectionRepository assetConnectionRepository;
	@Autowired
	AssetDetailsRepository assetDetailsRepository;
	@Autowired
	MeasurementDetailsRepository measurementDetailsRepository;
	@Autowired
	UuidRepository uuidRepository;

	// ASSET CRUD OPERATIONS
	public AssetDAOResponse save(AssetDAORequest asset) {
		if(asset.getId() !=  null && assetRepository.existsById(asset.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a asset with ID " + asset.getId());
		
		if (asset.getType() !=  null && assetTypeRepository.existsById(asset.getType())) {
			Asset assetEntity = asset.mapToEntity();
			assetEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));

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
		boolean assetExists = id != null && assetRepository.existsById(id);

		if ( assetExists && asset.getType() != null &&
				assetTypeRepository.existsById(asset.getType())) {
			asset.setId(id);
			return AssetDAOResponse.create(assetRepository.save(asset.mapToEntity()), null, null);
		} else throw new InvalidNonExistingIdException(assetExists
				? "The asset type doesn't exists"
				: "The asset to update doesn't exists");
	}

	public AssetDAOResponse connect(AssetConnectionDAORequest connection) {
		boolean assetExists = connection.getAssetId() != null && assetRepository.existsById(connection.getAssetId());

		if ( assetExists && assetRepository.existsById(connection.getAssetConnectedId())) {
			assetConnectionRepository.save(connection.mapToEntity());
			return AssetDAOResponse.create(assetRepository.findById(connection.getAssetId()).orElse(null), null, null);
		} else throw new InvalidNonExistingIdException("The assets to connect don't exists");
	}

	public MeasurementDAOResponse addMeasurement(Long assetId, Long measurementId) {
		if ( assetId != null && assetRepository.existsById(assetId) && 
				measurementId != null && measurementRepository.existsById(measurementId)) {
			Measurement measurement = measurementRepository.findById(measurementId).get();
			return MeasurementDAOResponse.create(measurementRepository.save(measurement), null);

		} else throw new InvalidNonExistingIdException(assetRepository.existsById(assetId) ? "The measurement doesn't exists" : "The asset doesn't exists");
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
					equals = asset.getType().getTypeCategory().equals(AssetTypeCategory.valueOf(filters.get("category")));
				}
				if (equals && filters.containsKey("location"))
					equals = asset.getLocation().equalsIgnoreCase(filters.get("location"));
//				if (equals && filters.containsKey("owner"))
//					equals = asset.getOwner() != null? String.valueOf(asset.getOwner().getId()).equalsIgnoreCase(filters.get("owner")) : false;
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

	public List<AssetDAOResponse> getByCategory(Long categoryId, long offset, int limit) {
		List<AssetDAOResponse> list = assetRepository.findByAssetCategory(categoryId, new OffSetPaging(offset, limit))
				.stream().map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset), measurementRepository.findByAsset(asset)))
				.collect(Collectors.toList());
		
		if (list != null && list.size() > 0)
			return list;
		else throw new NotFoundException("No asset found with asset category " + categoryId);
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
		if (type.getId() != null && assetTypeRepository.existsById(type.getId()))
			 throw new InvalidCreationIdAlreadyDefinedException("Type with id " + type.getId() + " already exists");
		else return assetTypeRepository.save(type);
	}

	public AssetType updateType(AssetType detail, Long id) {
		if ( id != null && assetTypeRepository.existsById(id)) {
			detail.setId(id);
			return assetTypeRepository.save(detail);
		} else throw new InvalidNonExistingIdException("No asset type found with id " + id);
	}

	public boolean deleteTypeById(Long id) {
		if (id != null && assetTypeRepository.existsById(id)) {
			assetTypeRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("No asset type found with id " + id);
	}

	public List<AssetType> getTypes(Map<String, String> filters, long offset, int limit) {
		Stream<AssetType> stream = assetTypeRepository.findAll(new OffSetPaging(offset, limit)).stream();
		List<AssetType> list;
		if (filters != null)
			list = stream.filter(type -> {
				boolean equals = true;

				if (filters.containsKey("name"))
					equals = type.getName().equalsIgnoreCase(filters.get("name")) ||
							type.getLabel().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("category"))
					equals = type.getTypeCategory().equals(AssetTypeCategory.valueOf(filters.get("category")));

				return equals;
			}).collect(Collectors.toList());
		else list = stream.collect(Collectors.toList());

		if (list != null && list.size() > 0)
			return list;
		else throw new NotFoundException("No assets found with asset category");		
	}

	public AssetType getTypeById(Long id) {
		return assetTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("No asset type with id " + id + " found"));
	}

	public AssetType getTypeByName(String name) {
		return assetTypeRepository.findByName(name).orElseThrow(() -> new NotFoundException("No asset type with name " + name + " found"));
	}

	public List<AssetDAOResponse> findByUserId(Long id, long offset, int limit){
		List<AssetDAOResponse> list = assetRepository.findByUserId(id, offset, limit).stream()
				.map(x -> AssetDAOResponse.create(x, assetRepository.findByParentAsset(x), measurementRepository.findByAsset(x)))
				.collect(Collectors.toList());

		if (list != null && list.size() > 0)
			return list;
		else throw new NotFoundException("No assets found relamted with user " + id + " found");
	}
	public List<SimpleAssetDAO> findSimpleByUserId(Long id, long offset, int limit){
		List<SimpleAssetDAO> list = assetRepository.findByUserId(id, offset, limit).stream()
				.map(x -> SimpleAssetDAO.create(x)).collect(Collectors.toList());

		if (list != null && list.size() > 0)
			return list;
		else throw new NotFoundException("No assets found related with user " + id + " found");
	}

	public List<AssetPanelDAO> findAssetsPanelsByUserId(Long id, long offset, int limit){
		List<AssetPanelDAO> list = assetRepository.findByUserId(id, offset, limit).stream()
				.map(x -> x.getInformationPanels().stream().map(y -> AssetPanelDAO.fromEntities(x, y)).collect(Collectors.toList()))
				.flatMap(List::stream).collect(Collectors.toList());

		if (list != null && list.size() > 0)
			return list;
		else throw new NotFoundException("No asset panels related with user " + id + " found");
	}

	// ASSETDETAILS CRUD OPERATIONS
	public AssetDetails saveDetail(AssetDetails detail) {
		detail.setId(null);
		return assetDetailsRepository.save(detail);
	}

	public AssetDetails updateDetail(AssetDetails detail, Long id) {
		if ( id != null && assetDetailsRepository.existsById(id)) {
			detail.setId(id);
			return assetDetailsRepository.save(detail);
		} else  throw new InvalidNonExistingIdException("No asset detail with id" + id + "found");
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
