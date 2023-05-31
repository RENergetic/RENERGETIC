package com.renergetic.userapi.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.userapi.model.AbstractMeter;
import com.renergetic.userapi.model.AbstractMeterConfig;
import com.renergetic.userapi.model.Domain;

import lombok.Data;

@Data
public class AbstractMeterDAO {
	@JsonProperty(required = false)
	Long id;
	
	@JsonProperty(required = true)
	String name;
	
	@JsonProperty(required = true)
	String formula;
	
	@JsonProperty(required = false)
	String condition;
	
	@JsonProperty(required = true)
	Domain domain;
	
	public static AbstractMeterDAO create(AbstractMeterConfig meter) {
		AbstractMeterDAO dao = null;
		
		if(meter != null) {
			dao = new AbstractMeterDAO();

			dao.setId(meter.getId());
			
			if (meter.getName() != null)
				dao.setName(meter.getName().meter);
			
			dao.setFormula(meter.getFormula());
			dao.setCondition(meter.getCondition());
			dao.setDomain(meter.getDomain());
		}
		
		return dao;
	}

	public AbstractMeterConfig mapToEntity() {
		AbstractMeterConfig entity = new AbstractMeterConfig();

		entity.setId(this.id);
		entity.setName(AbstractMeter.get(this.name));
		entity.setFormula(this.formula);
		entity.setCondition(this.condition);
		entity.setDomain(this.domain);
		
		return entity;
	}
	
}
