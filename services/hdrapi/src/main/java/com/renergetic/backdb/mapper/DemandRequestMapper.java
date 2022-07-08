package com.renergetic.backdb.mapper;

import com.renergetic.backdb.dao.DemandRequestDAO;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.DemandRequest;
import com.renergetic.backdb.model.Measurement;
import com.renergetic.backdb.model.MeasurementType;
import org.springframework.stereotype.Service;

@Service
public class DemandRequestMapper implements Mapper<DemandRequest, DemandRequestDAO> {

    @Override
    public DemandRequest toEntity(DemandRequestDAO dto) {
        DemandRequest entity = new DemandRequest();
//        entity.setId(dto.getId());
        entity.setUuid(dto.getUuid());

        Asset asset = new Asset();
        asset.setId(dto.getAssetId());
        entity.setAsset(asset);

        if(dto.getMeasurementId() != null){
            Measurement measurement = new Measurement();
            measurement.setId(dto.getMeasurementId());
            entity.setMeasurement(measurement);
        }

        if(dto.getMeasurementTypeId() != null){
            MeasurementType measurementType = new MeasurementType();
            measurementType.setId(dto.getMeasurementTypeId());
            entity.setMeasurementType(measurementType);
        }

        entity.setAction(dto.getAction());
        entity.setValue(dto.getValue());
        entity.setStart(dto.getStart());
        entity.setStop(dto.getStop());
        entity.setExt(dto.getExt());
        entity.setUpdateDate(dto.getUpdateDate());
        return entity;
    }

    @Override
    public DemandRequestDAO toDTO(DemandRequest entity) {
        DemandRequestDAO dao = new DemandRequestDAO();
//        dao.setId(entity.getId());
        dao.setUuid(entity.getUuid());
        dao.setAssetId(entity.getAsset().getId());
        if(entity.getMeasurement() != null)
            dao.setMeasurementId(entity.getMeasurement().getId());
        if(entity.getMeasurementType() != null)
            dao.setMeasurementTypeId(entity.getMeasurementType().getId());
        dao.setAction(entity.getAction());
        dao.setValue(entity.getValue());
        dao.setStart(entity.getStart());
        dao.setStop(entity.getStop());
        dao.setExt(entity.getExt());
        dao.setUpdateDate(entity.getUpdateDate());
        return dao;
    }
}