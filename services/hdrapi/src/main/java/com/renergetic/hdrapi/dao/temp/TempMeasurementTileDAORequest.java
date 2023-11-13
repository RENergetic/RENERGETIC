package com.renergetic.hdrapi.dao.temp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.MeasurementTileDAORequest;
import com.renergetic.common.dao.MeasurementTypeDAORequest;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.MeasurementType;
import lombok.*;

import java.util.Map;


public class TempMeasurementTileDAORequest {

    public static MeasurementTypeDAORequest create(MeasurementType m) {
        var mt = new MeasurementTypeDAORequest();
        mt.setId(m.getId());
        mt.setPhysicalName(m.getPhysicalName());
        return mt;
    }
    public static MeasurementTileDAORequest create(Measurement measurement) {
        return TempMeasurementTileDAORequest.create(measurement, null, null);
    }

    public static MeasurementTileDAORequest create(Measurement measurement, String function, Map<String, ?> props) {
        MeasurementTileDAORequest dao = null;
        if (measurement != null) {
            dao = new MeasurementTileDAORequest();

            dao.setId(measurement.getId());
            dao.setName(measurement.getName());
            dao.setSensorName(measurement.getSensorName());
            if (measurement.getType() != null)
                dao.setType(TempMeasurementTileDAORequest.create(measurement.getType()));

            //dao.setDescription(measurement.getDescription());
            //dao.setIcon(measurement.getIcon());
            dao.setDomain(measurement.getDomain());
            dao.setDirection(measurement.getDirection());
            dao.setProps(props);
            dao.setFunction(function);
        }
        return dao;
    }

}
