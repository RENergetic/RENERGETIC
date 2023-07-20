package com.renergetic.common.dao.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.details.MeasurementDetails;


public class MeasurementDetailsDAO extends DetailsDAO {
    @JsonProperty(value = "measurement_id", required = true)
    Long measurementId;


    public MeasurementDetailsDAO(String key, String value, Long measurementId) {
        super(key, value);
        this.measurementId = measurementId;
    }


    public static MeasurementDetailsDAO create(MeasurementDetails details) {
        MeasurementDetailsDAO dao = null;
        if (details != null) {
            dao = new MeasurementDetailsDAO(details.getKey(), details.getValue(), details.getId());
        }
        return dao;
    }



}
