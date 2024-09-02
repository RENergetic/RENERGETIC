package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.Measurement;
import com.renergetic.kpiapi.model.MeasurementType;
import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class MeasurementDAO {


    @JsonProperty(required = true)
    Long id;

    public static MeasurementDAO create(Measurement m) {
        var dao = new MeasurementDAO();
        dao.setId(m.getId());
        return dao;
    }
}
