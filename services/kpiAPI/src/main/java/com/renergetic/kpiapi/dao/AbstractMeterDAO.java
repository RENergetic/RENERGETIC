package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.*;

import lombok.Data;

@Data
public class AbstractMeterDAO {
    @JsonProperty(required = false)
    Long id;

    @JsonProperty(required = true)
    String name;
    @JsonProperty(required = false, value = "custom_name")
    String customName;

    @JsonProperty(required = true)
    String formula;

    @JsonProperty(required = false)
    String condition;

    @JsonProperty(required = true)
    Domain domain;


    @JsonProperty(required = false)
    MeasurementDAO measurement;
    public static AbstractMeterDAO create(AbstractMeterConfig meter) {
        return create(meter,null);
    }
    public static AbstractMeterDAO create(AbstractMeterConfig meter, Measurement m  ) {

        AbstractMeterDAO dao = null;

        if (meter != null) {
            dao = new AbstractMeterDAO();
            dao.customName = meter.getCustomName();

            dao.setId(meter.getId());

            if (meter.getName() != null)
                dao.setName(meter.getName().meter);

            dao.setFormula(meter.getFormula());
            dao.setCondition(meter.getCondition());
            dao.setDomain(meter.getDomain());
            if (m != null)
                dao.setMeasurement(MeasurementDAO.create(m));
        }

        return dao;
    }
    public AbstractMeterConfig mapToEntity( ){
        return mapToEntity(null);
    }
    public AbstractMeterConfig mapToEntity(Measurement m) {
        AbstractMeterConfig entity = new AbstractMeterConfig();

        entity.setId(this.id);
        entity.setName(AbstractMeter.obtain(this.name));
        entity.setCustomName(this.getCustomName());
        entity.setFormula(this.formula);
        entity.setCondition(this.condition);
        entity.setDomain(this.domain);
        if (m != null)
            entity.setMeasurement(m);

        return entity;
    }

}
