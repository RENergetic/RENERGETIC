package com.renergetic.baseapi.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.renergetic.common.dao.*;
import com.renergetic.common.repository.*;
import com.renergetic.common.dao.MeasurementDAOImpl;
import com.renergetic.common.dao.ResourceDAOImpl;
import com.renergetic.common.dao.details.MeasurementTagsDAO;
import com.renergetic.common.dao.details.TagDAO;
import com.renergetic.common.dao.MeasurementDAORequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Direction;
import com.renergetic.common.model.Domain;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.model.UUID;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import com.renergetic.common.repository.information.MeasurementDetailsRepository;
import com.renergetic.common.utilities.OffSetPaging;

import io.smallrye.common.constraint.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MeasurementService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    MeasurementTypeRepository measurementTypeRepository;
    ;
    @Autowired
    AssetRepository assetRepository;

    @Autowired
    MeasurementDetailsRepository measurementDetailsRepository;
    @Autowired
    MeasurementTagsRepository measurementTagsRepository;
    @Autowired
    UuidRepository uuidRepository;

    /**
     * check if the input measurement type matches database
     *
     * @param dao
     * @return
     */
    public MeasurementType verifyMeasurementType(MeasurementType dao) {
        MeasurementType dbType = dao.getId() != null ? measurementTypeRepository.findById(dao.getId()).orElse(null) : null;
        if (dbType == null
                || (dbType.getName() == null && dao.getName() != null)
                || !dbType.getName().equals(dao.getName())
                || (dbType.getPhysicalName() == null && dao.getPhysicalName() != null)
                || !dbType.getPhysicalName().equals(dao.getPhysicalName())
                || (dbType.getUnit() == null && dao.getUnit() != null)
                || !dbType.getUnit().equals(dao.getUnit())
        ) {
            List<MeasurementType> types =
                    measurementTypeRepository.findMeasurementType(dao.getName(),
                            dao.getUnit(), dao.getPhysicalName());
            if (types.size() != 1) {
                throw new InvalidCreationIdAlreadyDefinedException("Measurement type does not exists");
            }
            return types.get(0);
        }
        return dbType;
    }

    public MeasurementTypeDAORequest verifyMeasurementType(MeasurementTypeDAORequest dao) {
        MeasurementType dbType = dao.getId() != null ? measurementTypeRepository.findById(dao.getId()).orElse(null) : null;
        if (dbType == null
                || (dbType.getName() == null && dao.getName() != null)
                || !dbType.getName().equals(dao.getName())
                || (dbType.getPhysicalName() == null && dao.getPhysicalName() != null)
                || !dbType.getPhysicalName().equals(dao.getPhysicalName())
                || (dbType.getUnit() == null && dao.getUnit() != null)
                || !dbType.getUnit().equals(dao.getUnit())
        ) {
            List<MeasurementType> types =
                    measurementTypeRepository.findMeasurementType(dao.getName(),
                            dao.getUnit(), dao.getPhysicalName());
            if (types.size() != 1) {
                throw new InvalidCreationIdAlreadyDefinedException("Measurement type does not exists");
            }
            return MeasurementTypeDAORequest.create(types.get(0));
        }
        return MeasurementTypeDAORequest.create(dbType);
    }

    // ASSET CRUD OPERATIONS
    public MeasurementDAOResponse save(MeasurementDAORequest measurement) {
        if (measurement.getId() != null && measurementRepository.existsById(measurement.getId()))
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Already exists a measurement with ID " + measurement.getId());
        measurement.setType(verifyMeasurementType(measurement.getType()));
        SimpleAssetDAO assetDAO = measurement.getAsset();
        if (assetDAO != null) {
            Asset asset = assetRepository.findById(assetDAO.getId()).orElse(null);
            if (asset == null || !asset.getName().equals(assetDAO.getName())) {
                measurement.setAsset(SimpleAssetDAO.create(
                        assetRepository.findByName(assetDAO.getName()).orElseThrow(() ->
                                new NotFoundException("Asset does not exist " + assetDAO.getName() + " found"))));
            }
        }
        Measurement measurementEntity = measurement.mapToEntity();
        measurementEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        measurementRepository.save(measurementEntity);
        if (measurementEntity.getDetails() != null) {
            measurementEntity.getDetails().forEach(
                    it -> this.setProperty(measurementEntity.getId(), it)
            );
        }
        return MeasurementDAOResponse.create(measurementRepository.save(measurementEntity), null, null);
    }

    @Transactional
    public List<MeasurementDAOResponse> save(List<MeasurementDAORequest> measurements) {
        var filtered = measurements.stream().filter(it -> {
            Long typeId = null;
            if (it.getType() != null) {
                var t = measurementTypeRepository.findMeasurementType(it.getType().getName(),
                        it.getType().getUnit(), it.getType().getPhysicalName()).stream().findFirst();
                if (t.isPresent()) {
                    typeId = t.get().getId();
                }
            }
            return !measurementRepository.findMeasurements(
                    null, null, it.getName(), it.getSensorName(),
                    it.getDomain() != null ? it.getDomain().name() : null, it.getDirection() != null ? it.getDirection().name() : null,
                    typeId, null, 0, 1).isEmpty();

        });
        return filtered.map(this::save).collect(Collectors.toList());
    }

    public MeasurementDAOResponse duplicate(Long id) {
        Measurement measurementToCopy = measurementRepository.findById(id).orElseThrow(InvalidNonExistingIdException::new);
        MeasurementDAORequest dao = MeasurementDAORequest.create(measurementToCopy);
        dao.setId(null);
        Measurement measurementEntity = dao.mapToEntity();

        measurementEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
        return MeasurementDAOResponse.create(measurementRepository.save(measurementEntity), null, null);
    }

    public boolean deleteById(Long id) {
        var m = measurementRepository.findById(id)
                .orElseThrow(() -> new InvalidNonExistingIdException("No measurement with id " + id + " found"));
        m.getDetails().forEach((it) -> measurementDetailsRepository.delete(it));
        measurementTagsRepository.findByMeasurementId(m.getId()).forEach(
                (it) -> measurementTagsRepository.delete(it));
        measurementRepository.deleteById(id);
        return true;
    }

    public MeasurementDAOResponse update(MeasurementDAORequest measurement, Long id) {
        requireMeasurement(id);
        measurement.setId(id);
        return MeasurementDAOResponse.create(measurementRepository.save(measurement.mapToEntity()), null, null);

    }

    public boolean setProperty(Long measurementId, MeasurementDetails details) {
        details.setMeasurement(new Measurement(measurementId, null, null, null, null, null, null));

        log.warn(String.format("Saving %s %s", details.getKey(), details.getValue()));
        MeasurementDetails previousDetails = measurementDetailsRepository.findByKeyAndMeasurementId(details.getKey(),
                details.getMeasurement().getId()).orElse(null);
        if (previousDetails != null) {
            previousDetails.setValue(details.getValue());
            return measurementDetailsRepository.save(previousDetails) != null;
        }

        return measurementDetailsRepository.save(details) != null;
    }

    public boolean setProperties(Long measurementId, Map<String, String> properties) {
        this.getById(measurementId);//check is exists
        properties.forEach((k, v) -> log.error("Sent: " + k + " " + v));
        return properties.entrySet().stream().map(it ->
                this.setProperty(measurementId, new MeasurementDetails(it.getKey(), it.getValue(), measurementId))
        ).filter(it -> it).allMatch(it -> it);
    }

    public List<MeasurementDAOResponse> getByProperty(String key, String value, long offset, long limit) {
        List<Measurement> measurements = measurementRepository.getByProperty(key, value, offset, limit);
        Stream<Measurement> stream = measurements.stream();
        List<MeasurementDAOResponse> list;
        list = stream
                .map(measurement -> MeasurementDAOResponse.create(measurement,
                        measurementDetailsRepository.findByMeasurementId(measurement.getId()), null))
                .collect(Collectors.toList());
        return list;
    }

    public List<MeasurementDAOResponse> getByTag(String key, String value, long offset, long limit) {
        List<Measurement> measurements = measurementRepository.getMeasurementByTag(key, value, offset, limit);
        Stream<Measurement> stream = measurements.stream();
        List<MeasurementDAOResponse> list;
        list = stream
                .map(measurement -> MeasurementDAOResponse.create(measurement,
                        measurementDetailsRepository.findByMeasurementId(measurement.getId()), null))
                .collect(Collectors.toList());
        return list;
    }

    public List<MeasurementDAOImpl> findMeasurements(String measurementName, String domain, String direction,
                                                     String sensorName,
                                                     String assetName, Long typeId, String physicalTypeName,
                                                     String tagKey,
                                                     String tagValue,
                                                     Long offset,
                                                     Integer limit) {
        Stream<MeasurementDAO> measurements;
        if (tagKey == null) {
            measurements = measurementRepository.findMeasurements(
                    null, assetName, measurementName, sensorName, domain, direction, typeId, physicalTypeName, offset,
                    limit).stream();
        } else {
            measurements = measurementRepository.findMeasurementsByTag(
                    null, assetName, measurementName, sensorName, domain, direction, typeId, physicalTypeName, tagKey,
                    tagValue, offset, limit).stream();
        }

        return measurements.map(MeasurementDAOImpl::create).collect(Collectors.toList());
    }

    public MeasurementDAOImpl findMeasurement(Long id) {
        MeasurementDAO measurement = measurementRepository.findMeasurement(id)
                .orElseThrow(() -> new NotFoundException("Measurement not found"));

        return MeasurementDAOImpl.create(measurement);
    }

    public List<MeasurementDAOImpl> findAssetMeasurements(
            Long assetId,
            Long offset,
            Integer limit) {
        Stream<MeasurementDAO> measurements;

        measurements = measurementRepository.findMeasurements(
                assetId, null, null, null, null, null, null, null, offset, limit).stream();
        List<MeasurementDAOImpl> collect = measurements.map(MeasurementDAOImpl::create).collect(Collectors.toList());
        collect.forEach(it ->
        {
            List<MeasurementTagsDAO> tags = measurementTagsRepository.findByMeasurementId(it.getId())
                    .stream().map(tag -> MeasurementTagsDAO.create(tag, it.getId()))
                    .collect(Collectors.toList());
            it.setTags(tags);
        });

        return collect;
    }

    public List<MeasurementDAOResponse> get(Map<String, String> filters, long offset, int limit) {
        Page<Measurement> measurements = measurementRepository.findAll(new OffSetPaging(offset, limit));

        Stream<Measurement> stream = measurements.stream();
        List<MeasurementDAOResponse> list;

        if (filters != null)
            list = stream.filter(measurement -> {
                        boolean equals = true;

                        if (filters.containsKey("name")) {
                            equals = (measurement.getName().toLowerCase().startsWith(filters.get("name").toLowerCase()) ||
                                    (measurement.getLabel() != null) && measurement.getLabel().toLowerCase().startsWith(
                                            filters.get("name").toLowerCase()));
                        }
                        if (equals && filters.containsKey("type"))
                            equals = measurement.getType().getName().equalsIgnoreCase(filters.get("type"));
                        if (equals && filters.containsKey("direction"))
                            equals = measurement.getDirection().equals(Direction.valueOf(filters.get("direction")));
                        if (equals && filters.containsKey("domain") && measurement.getAsset() != null) {
                            equals = measurement.getDomain().equals(Domain.valueOf(filters.get("domain")));
                        }

                        return equals;
                    }).map(measurement -> MeasurementDAOResponse.create(measurement,
                            measurementDetailsRepository.findByMeasurementId(measurement.getId()), null))
                    .collect(Collectors.toList());
        else
            list = stream
                    .map(measurement -> MeasurementDAOResponse.create(measurement,
                            measurementDetailsRepository.findByMeasurementId(measurement.getId()), null))
                    .collect(Collectors.toList());

        if (list != null && !list.isEmpty())
            return list;
        else throw new NotFoundException("No measurements found");
    }

    public List<MeasurementDAOResponse> find(Map<String, String> filters, long offset, int limit) {
        //TODO: more filtering options
        String s = filters.getOrDefault("name", null);
        String l = filters.getOrDefault("label", null);
        List<Measurement> list = measurementRepository.filterMeasurement(s, l, offset, limit);
        if (!list.isEmpty())
            return list.stream().map(it -> MeasurementDAOResponse.create(it,
                            measurementDetailsRepository.findByMeasurementId(it.getId()), null))
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }

    public Measurement getById(Long id) {
        requireMeasurement(id);
        return measurementRepository.findById(id).orElseThrow(InvalidNonExistingIdException::new);
    }


    // MEASUREMENTTYPE CRUD OPERATIONS
    public MeasurementType saveType(MeasurementType type) {
        if (type.getId() != null && measurementTypeRepository.existsById(type.getId()))
            throw new InvalidCreationIdAlreadyDefinedException(
                    "Already exists a measurement type with ID " + type.getId());

        return measurementTypeRepository.save(type);
    }

    public Boolean setDashboardVisibility(long id, boolean visibility) {
        Optional<MeasurementType> type = measurementTypeRepository.findById(id);
        if (type.isEmpty())
            throw new InvalidNonExistingIdException("The Type with ID " + id + " doesn't exists");
        MeasurementType entity = type.get();
        entity.setDashboardVisibility(visibility);
        return measurementTypeRepository.save(entity).getDashboardVisibility();
    }

    public MeasurementType updateType(MeasurementType detail, Long id) {
        if (id != null && measurementTypeRepository.existsById(id)) {

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
        Page<MeasurementType> types = measurementTypeRepository.findAll(new OffSetPaging(offset, limit,
                Sort.by(Sort.Direction.ASC, "id", "physicalName")));
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

        if (list != null && !list.isEmpty())
            return list;
        else throw new NotFoundException("No measurement types found");
    }

    public MeasurementType getTypeById(Long id) {
        return measurementTypeRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No measurement types with id " + id + " found"));
    }

    // MEASUREMENTTAGS CRUD OPERATIONS
    public MeasurementTags saveTag(MeasurementTags tag) {
        if (tag.getId() != null && measurementTagsRepository.existsById(tag.getId()))
            throw new InvalidCreationIdAlreadyDefinedException("Already exists a tag with ID " + tag.getId());
        if (!measurementTagsRepository.findByKeyAndValue(tag.getKey(), tag.getValue()).isEmpty())
            throw new InvalidCreationIdAlreadyDefinedException("Tag already exists" + tag.getKey() + "=" + tag.getValue());
        return measurementTagsRepository.save(tag);
    }

    public boolean setTags(Long measurementId, Map<String, String> tags) {
        if (measurementRepository.existsById(measurementId)) {
            List<MeasurementTags> tagList = tags.entrySet().stream().map(it -> {
                var l = measurementTagsRepository.findByKeyAndValue(it.getKey(), it.getValue());
                if (l.isEmpty()) {
                    throw new InvalidNonExistingIdException(
                            "No tag with key " + it.getKey() + " and value: " + it.getValue());
                }
                return l.get(0);
            }).toList();
            measurementTagsRepository.clearTags(measurementId);
            Optional<Boolean> hasFail =
                    tagList.stream().map(it -> measurementTagsRepository.setTag(it.getId(), measurementId) == 0).filter(
                            it -> it).findAny();
            if (hasFail.isPresent()) {
                //TODO: rollback
            }
        } else throw new InvalidNonExistingIdException("Measurement with id %s no exists", measurementId);
        return true;
    }

    public boolean setTags(Long measurementId, String key, String value) {
        requireMeasurement(measurementId);
        this.deleteMeasurementTag(measurementId, key);
        List<MeasurementTags> tags = measurementTagsRepository.findByKeyAndValue(key, value);
        if (tags.isEmpty()) {
            throw new InvalidNonExistingIdException("No tag with key " + key + " and value: " + value);
        }
        MeasurementTags tag = tags.get(0);
        measurementTagsRepository.setTag(tag.getId(), measurementId);
        return true;
    }

    public boolean setTag(Long measurementId, String key, String value) {
        requireMeasurement(measurementId); //delete all other tags related with the measurement
        var l = measurementTagsRepository.findByKeyAndValue(key, value); //Check if tag is defined in the tags table
        if (l.isEmpty()) {
            throw new InvalidNonExistingIdException("No tag with key " + key + " and value: " + value);
        }
        this.deleteMeasurementTag(measurementId, key);
        var tag = l.get(0);
        measurementTagsRepository.setTag(tag.getId(), measurementId);
        return true;
    }

    public MeasurementTags updateTag(MeasurementTags tag, Long id) {
        if (id != null && measurementTagsRepository.existsById(id)) {
            tag.setId(id);
            return measurementTagsRepository.save(tag);
        } else throw new InvalidNonExistingIdException("No tag with id " + id + "found");
    }

    public boolean deleteMeasurementTag(Long measurementId, String tagKey) {
        return measurementTagsRepository.clearTag(measurementId, tagKey) == 1;
    }

    public boolean deleteTagById(Long id) {
        if (id != null && measurementTagsRepository.existsById(id)) {
            measurementTagsRepository.deleteById(id);
            return true;
        } else throw new InvalidNonExistingIdException("No tag with id " + id + "found");
    }

    public void deleteTag(String key, String value) {
        List<MeasurementTags> l = measurementTagsRepository.findByKeyAndValue(key, value);
        if (l.isEmpty()) {
            throw new InvalidNonExistingIdException("No tag with key " + key + " and value: " + value);
        }
        MeasurementTags t = l.get(0);
        List<Measurement> m =
                measurementRepository.getMeasurementByTagId(t.getId(), 0L, 1L);
        if (!m.isEmpty())
            throw new InvalidCreationIdAlreadyDefinedException("Tag " + key + ":" + value + "  is used ");

        measurementTagsRepository.delete(t);


    }

    public List<TagDAO> getTagsFilter(Map<String, String> filters, long offset, int limit) {
        Page<MeasurementTags> tags = measurementTagsRepository.findAll(new OffSetPaging(offset, limit));
        Stream<MeasurementTags> stream = tags.stream();

        if (filters != null)
            stream = stream.filter(detail -> {
                boolean equals = true;

                if (filters.containsKey("key"))
                    equals = detail.getKey().equalsIgnoreCase(filters.get("key"));

                return equals;
            });
        return stream.map(it -> new TagDAO(it.getKey(), it.getValue(), it.getId())).collect(Collectors.toList());
    }

    public List<TagDAO> getTags(@NotNull String key, long offset, int limit) {

        Stream<MeasurementTags> stream;
        if (key == null) stream = measurementTagsRepository.findAll(new OffSetPaging(offset, limit)).stream();
        else stream = measurementTagsRepository.findByKey(key).stream();

        return stream.map(it -> new TagDAO(it.getKey(), it.getValue(), it.getId())).collect(Collectors.toList());
    }

    public TagDAO getTag(@NotNull String key, String value) {
        //delete all other tags related with the measurement
        var l = measurementTagsRepository.findByKeyAndValue(key, value); //Check if tag is defined in the tags table
        if (l.isEmpty()) {
            return null;
        }
        var tag = l.get(0);
        return new TagDAO(tag.getKey(), tag.getValue(), tag.getId());
    }

    public List<MeasurementTagsDAO> getMeasurementTags(Long measurementId) {
        List<MeasurementTags> tags = measurementRepository.getTags(measurementId);
        return tags.stream().map(it -> MeasurementTagsDAO.create(it)).collect(Collectors.toList());

    }

    public List<String> getTagKeys() {
        return measurementTagsRepository.listTagKeys();
    }

    public List<String> getTagValues(String tagKey) {
        return measurementTagsRepository.listTagValues(tagKey);
    }

    public MeasurementTags getTagById(Long id) {
        return measurementTagsRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No tags with id " + id + " found"));
    }

    // MEASUREMENTDETAILS CRUD OPERATIONS
    public MeasurementDetails saveDetail(MeasurementDetails detail) {
        detail.setId(null);
        if (measurementDetailsRepository.existsByKeyAndMeasurementId(detail.getKey(), detail.getMeasurement().getId()))
            throw new InvalidCreationIdAlreadyDefinedException(
                    String.format("Already exists a detail with key %s referring to measurement %s ", detail.getKey(),
                            detail.getMeasurement().getId()));

        return measurementDetailsRepository.save(detail);
    }

    public MeasurementDetails updateDetail(MeasurementDetails detail) {
        MeasurementDetails entity = measurementDetailsRepository.findByKeyAndMeasurementId(detail.getKey(),
                detail.getMeasurement().getId()).orElse(null);
        if (entity != null) {
            detail.setId(entity.getId());
            return measurementDetailsRepository.save(detail);
        } else throw new InvalidNonExistingIdException(
                String.format("Already exists a detail with key %s referring to measurement %s ", detail.getKey(),
                        detail.getMeasurement().getId()));
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
            stream.filter(detail -> {
                boolean equals = true;

                if (filters.containsKey("key"))
                    equals = detail.getKey().equalsIgnoreCase(filters.get("key"));

                return equals;
            });
        List<MeasurementDetails> list = stream.collect(Collectors.toList());

        if (list != null && !list.isEmpty())
            return list;
        else throw new NotFoundException("No tags found");
    }

    public MeasurementDetails getDetailById(Long id) {
        return measurementDetailsRepository.findById(id).orElseThrow(
                () -> new NotFoundException("No measurement details with id " + id + " found"));
    }

    public List<MeasurementDetails> getDetailsByMeasurementId(Long id) {

        var x = measurementDetailsRepository.findByMeasurementId(id);
        if (x == null) {
            return Collections.emptyList();
        }
        return x;

    }


    public List<ResourceDAO> listLinkedPanels(Long id) {
        return measurementRepository.getLinkedPanels(id).stream().map(
                it -> ResourceDAOImpl.create(it.getId(), it.getName(), it.getLabel())
        ).collect(Collectors.toList());
    }

    private boolean requireMeasurement(Long id) {
        if (!measurementRepository.existsById(id))
            throw new InvalidNonExistingIdException("The measurement:" + id + " doesn't exists");
        return true;
    }

}
