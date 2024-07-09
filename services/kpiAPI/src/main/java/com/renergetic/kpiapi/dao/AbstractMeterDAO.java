package com.renergetic.kpiapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;

import com.renergetic.kpiapi.model.MeasurementType;
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
    //type is used to convert units of the  data put into influx
    @JsonProperty(required = false)
    MeasurementType type;

    public static AbstractMeterDAO create(AbstractMeterConfig meter) {

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
            dao.setType(meter.getType());
        }

        return dao;
    }

    public AbstractMeterConfig mapToEntity() {
        AbstractMeterConfig entity = new AbstractMeterConfig();

        entity.setId(this.id);
        entity.setName(AbstractMeter.obtain(this.name));
        entity.setCustomName(this.getCustomName());
        entity.setFormula(this.formula);
        entity.setCondition(this.condition);
        entity.setDomain(this.domain);
        entity.setType(this.type);

        return entity;
    }

}
