package com.renergetic.hdrapi.service;

import com.renergetic.common.dao.*;
import com.renergetic.common.exception.InvalidArgumentException;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.*;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.*;
import com.renergetic.common.repository.information.AssetDetailsRepository;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
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
    AssetCategoryRepository assetCategoryRepository;
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
        if (asset.getId() != null && assetRepository.existsById(asset.getId()))
            throw new InvalidCreationIdAlreadyDefinedException("Already exists a asset with ID " + asset.getId());

        if (asset.getType() != null && assetTypeRepository.existsById(asset.getType())) {
            Asset assetEntity = asset.mapToEntity();
            assetEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));

            return AssetDAOResponse.create(assetRepository.save(assetEntity), null, null);
        } else throw new InvalidArgumentException("The asset type doesn't exists");
    }

    public boolean deleteById(Long id) {
        if (id != null && assetRepository.existsById(id)) {
            assetRepository.deleteById(id);
            return true;
        } else throw new InvalidNonExistingIdException("The asset to delete doesn't exists");
    }

    public AssetDAOResponse update(AssetDAORequest asset, Long id) {
        boolean assetExists = id != null && assetRepository.existsById(id);

        if (assetExists && asset.getType() != null &&
                assetTypeRepository.existsById(asset.getType())) {
            asset.setId(id);
            assetRepository.update(asset.mapToEntity());
            return AssetDAOResponse.create(assetRepository.findById(id).orElse(null), null, null);
        } else throw new InvalidNonExistingIdException(assetExists
                ? "The asset type doesn't exists"
                : "The asset to update doesn't exists");
    }

    public AssetDAOResponse connect(AssetConnectionDAORequest connection) {
        boolean assetExists = connection.getAssetId() != null && assetRepository.existsById(connection.getAssetId());

        if (assetExists && assetRepository.existsById(connection.getAssetConnectedId())) {
            assetConnectionRepository.save(connection.mapToEntity());
            return AssetDAOResponse.create(assetRepository.findById(connection.getAssetId()).orElse(null), null, null);
        } else throw new InvalidNonExistingIdException("The assets to connect don't exists");
    }

    public MeasurementDAOResponse addMeasurement(Long assetId, Long measurementId) {
        if (assetId != null && assetRepository.existsById(assetId) &&
                measurementId != null && measurementRepository.existsById(measurementId)) {
            Measurement measurement = measurementRepository.findById(measurementId).get();

            return MeasurementDAOResponse.create(measurementRepository.save(measurement), null,null);

        } else throw new InvalidNonExistingIdException(
                assetRepository.existsById(assetId) ? "The measurement doesn't exists" : "The asset doesn't exists");
    }

    public List<AssetDAOResponse> get(Map<String, String> filters, long offset, int limit) {

        Stream<Asset> stream = assetRepository.filterAssets(
                filters.getOrDefault("name", null),
                filters.getOrDefault("label", null),
                filters.getOrDefault("category", null),
                filters.getOrDefault("type", null),
                offset, limit).stream();
        List<AssetDAOResponse> assets;
        assets = stream.map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset),
                        measurementRepository.findByAsset(asset)))
                .collect(Collectors.toList());
        return assets;
    }

    public AssetDAOResponse getById(Long id) {
        Asset asset = assetRepository.findById(id).orElse(null);

        if (asset != null)
            return AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset),
                    measurementRepository.findByAsset(asset));
        else throw new NotFoundException("No asset found related with id " + id);
    }

    public List<AssetDAOResponse> getByCategory(Long categoryId, long offset, int limit) {
        List<AssetDAOResponse> list = assetRepository.findByAssetCategoryId(categoryId, new OffSetPaging(offset, limit))
                .stream().map(asset -> AssetDAOResponse.create(asset, assetRepository.findByParentAsset(asset),
                        measurementRepository.findByAsset(asset)))
                .collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No asset found with asset category " + categoryId);
    }

    /**
     * Get the asset connected to the asset with the given id
     *
     * @param id Asset ID used to search asset connected to it
     * @return A AssetDAOResponse list with all connected assets and its connection types
     */
    public List<AssetDAOResponse> getConnectedTo(Long id) {
        Asset asset = assetRepository.findById(id).orElse(null);

        if (asset == null)
            throw new NotFoundException("No asset found related with id" + id);
        else if (asset.getConnections() != null && asset.getConnections().size() > 0)
            return asset.getConnections().stream()
                    .map(obj -> {
                        AssetDAOResponse dao = AssetDAOResponse.create(obj.getConnectedAsset(),
                                assetRepository.findByParentAsset(obj.getConnectedAsset()),
                                measurementRepository.findByAsset(obj.getConnectedAsset()));
                        dao.setConnectionType(obj.getConnectionType());
                        return dao;
                    })
                    .collect(Collectors.toList());
        else {
            System.err.println("MAL");
            return new ArrayList<>();
        }
    }

    public List<MeasurementDAOResponse> getMeasurements(Long id) {
        Asset asset = assetRepository.findById(id).orElse(null);

        if (asset == null)
            throw new NotFoundException("No asset found related with id" + id);
        else {
            List<Measurement> measurements = measurementRepository.findByAsset(asset);

            if (measurements != null && measurements.size() > 0)
                return measurements.stream()
                        .map(obj -> MeasurementDAOResponse.create(obj,
                                measurementDetailsRepository.findByMeasurementId(obj.getId()),null))
                        .collect(Collectors.toList());
            else return new ArrayList<>();
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
        if (id != null && assetTypeRepository.existsById(id)) {
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
                return equals;
            }).collect(Collectors.toList());
        else list = stream.collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No assets types found");
    }

    public AssetType getTypeById(Long id) {
        return assetTypeRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No asset type with id " + id + " found"));
    }

    public AssetType getTypeByName(String name) {
        return assetTypeRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("No asset type with name " + name + " found"));
    }

    public List<AssetDAOResponse> findByUserId(Long id, long offset, int limit) {
        List<AssetDAOResponse> list = assetRepository.findByUserId(id, offset, limit).stream()
                .map(x -> AssetDAOResponse.create(x, assetRepository.findByParentAsset(x),
                        measurementRepository.findByAsset(x)))
                .collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No assets found related with user " + id + " found");
    }

    public List<SimpleAssetDAO> findSimpleByUserId(Long id, List<ConnectionType> connectionTypes, long offset,
                                                   int limit) {
        List<SimpleAssetDAO> list = assetRepository.findByUserIdConnectionTypes(id,
                        connectionTypes.stream().map(ConnectionType::toString).collect(Collectors.toList()),
                        offset, limit).stream()
                .map(x -> SimpleAssetDAO.create(x)).collect(Collectors.toList());

        if (list != null && list.size() > 0)
            return list;
        else throw new NotFoundException("No assets found related with user " + id + " found");
    }


    public List<AssetPanelDAO> findAssetsPanelsByUserId(Long userId, List<ConnectionType> connectionTypes, long offset,
                                                        int limit) {

        List<AssetPanelDAO> list =
                assetRepository.findByUserIdConnectionTypes(userId,
                                connectionTypes.stream().map(ConnectionType::toString).collect(Collectors.toList()),
                                offset, limit).stream()
                        .map(x -> x.getInformationPanels().stream().filter(InformationPanel::getFeatured).map(y -> AssetPanelDAO.fromEntities(x, y)).collect(
                                Collectors.toList()))
                        .flatMap(List::stream).collect(Collectors.toList());

        return list;
    }

    // ASSETDETAILS CRUD OPERATIONS
    public AssetDetails saveDetail(AssetDetails detail, Long assetId) {
        detail.setId(null);
        if (assetId != null && assetRepository.existsById(assetId)) {
            if (assetDetailsRepository.existsByKeyAndAssetId(detail.getKey(), assetId))
                throw new InvalidCreationIdAlreadyDefinedException("There are details with the same key and asset id");

            Asset asset = new Asset();
            asset.setId(assetId);
            detail.setAsset(asset);
            return assetDetailsRepository.save(detail);
        } else throw new InvalidNonExistingIdException("No asset with id " + assetId + " found");
    }

    public AssetDetails updateDetail(AssetDetails detail, Long id, Long assetId) {
        if (id != null && assetDetailsRepository.existsByIdAndAssetId(id, assetId)) {
            if (assetDetailsRepository.existsByKeyAndAssetId(detail.getKey(), assetId))
                throw new InvalidCreationIdAlreadyDefinedException("There are details with the same key and asset id");
            detail.setId(id);
            Asset asset = new Asset();
            asset.setId(assetId);
            detail.setAsset(asset);
            return assetDetailsRepository.save(detail);
        } else throw new InvalidNonExistingIdException(
                "No asset detail with id " + id + " related with " + assetId + " found");
    }

    public AssetDetails updateDetailByKey(AssetDetails detail, Long assetId) {
        AssetDetails entity = assetDetailsRepository.findByKeyAndAssetId(detail.getKey(), assetId).orElse(null);
        if (entity != null) {
            detail.setId(entity.getId());
            Asset asset = new Asset();
            asset.setId(assetId);
            detail.setAsset(asset);
            return assetDetailsRepository.save(detail);
        } else throw new InvalidNonExistingIdException(
                "No asset detail with key " + detail.getKey() + " related with " + assetId + " found");
    }

    public AssetDAOResponse updateAssetCategory(AssetCategoryDAO category,Long assetId) {
    	Asset asset = assetRepository.findById(assetId).orElse(null);
    	AssetCategory assetCategory = assetCategoryRepository.getById(category.getId());
    	if (asset!=null && assetCategory != null) {
    		asset.setAssetCategory(assetCategory);
    		return AssetDAOResponse.create(assetRepository.save(asset),null);
    	} else throw new InvalidNonExistingIdException(asset == null
    		? "The asset to update doens´t exist" 
    		:"The assetcategory to update doesn't exist");
    }
    
    public AssetDAOResponse deleteAssetCategory(Long assetId) {
    	Asset asset = assetRepository.findById(assetId).orElse(null);
    	if (asset!=null) {
    		asset.setAssetCategory(null);
    		return AssetDAOResponse.create(assetRepository.save(asset),null);
    	} else throw new InvalidNonExistingIdException("The asset to update doesn't exists");
    }
    
    public boolean deleteDetailById(Long id, Long assetId) {
        if (id != null && assetDetailsRepository.existsByIdAndAssetId(id, assetId)) {
            assetDetailsRepository.deleteById(id);
            return true;
        } else throw new InvalidNonExistingIdException(
                "No asset detail with id " + id + " related with " + assetId + " found");
    }

    public boolean deleteDetailByKey(String key, Long assetId) {
        if (key != null && assetDetailsRepository.existsByKeyAndAssetId(key, assetId)) {
            assetDetailsRepository.deleteByKeyAndAssetId(key, assetId);
            return true;
        } else throw new InvalidNonExistingIdException(
                "No asset detail with key " + key + " related with " + assetId + " found");
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
        return assetDetailsRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No asset detais found related with id " + id));
    }

    public List<AssetDetails> getDetailsByAssetId(Long id) {
        List<AssetDetails> details = assetDetailsRepository.findByAssetId(id);

        if (details != null && details.size() > 0)
            return details;
        else if (assetRepository.existsById(id)) return new ArrayList<>();
        else throw new NotFoundException("Asset " + id + "  not found");
    }

    public List<AssetTypeDAO> listTypes() {
        return assetTypeRepository.findAll().stream().map(
                AssetTypeDAO::create
        ).collect(Collectors.toList());
    }

    public List<AssetCategoryDAO> listCategories() {
        return assetCategoryRepository.findAll().stream().map(
                AssetCategoryDAO::create
        ).collect(Collectors.toList());
    }
}
