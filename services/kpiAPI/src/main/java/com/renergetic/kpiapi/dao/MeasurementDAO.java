package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.Measurement;
import lombok.Data;

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
