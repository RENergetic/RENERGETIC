package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Measurement;
import lombok.Data;

@Data
public class MeasurementKPIDAO {


    @JsonProperty(required = true)
    Long id;

    public static MeasurementKPIDAO create(Measurement m) {
        var dao = new MeasurementKPIDAO();
        dao.setId(m.getId());
        return dao;
    }
}
