package com.renergetic.kpiapi.dao;

import java.util.TreeMap;

import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;

public class AbstractMeterDataDAO extends DataDAO<AbstractMeter> {
	public AbstractMeterDataDAO() {
		data = new TreeMap<>();
	}
	
	public static AbstractMeterDataDAO create(AbstractMeterConfig config) {
		AbstractMeterDataDAO data = new AbstractMeterDataDAO();
		data.setName(config.getName());
		data.setDomain(config.getDomain());
		
		return data;
	}
}
