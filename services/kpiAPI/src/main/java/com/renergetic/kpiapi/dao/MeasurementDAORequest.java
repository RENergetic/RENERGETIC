package com.renergetic.kpiapi.dao;

import java.util.HashMap;
import java.util.Map;

import com.renergetic.kpiapi.model.AbstractMeterConfig;

import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.model.KPI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeasurementDAORequest {
	String bucket;
	String measurement;
	Map<String, String> fields;
	Map<String, String> tags;
	
	public static MeasurementDAORequest create(AbstractMeterConfig config) {
		MeasurementDAORequest dao = new MeasurementDAORequest();
		
		dao.measurement = config.getName().name();
		dao.fields = new HashMap<>();
		dao.tags = new HashMap<>();
		dao.tags.put("domain", config.getDomain().name());
		
		return dao;
	}

	public static MeasurementDAORequest create(KPI kpi, Domain domain) {
		MeasurementDAORequest dao = new MeasurementDAORequest();

		dao.measurement = kpi.name();
		dao.fields = new HashMap<>();
		dao.tags = new HashMap<>();
		dao.tags.put("domain", domain.name());

		return dao;
	}
}
