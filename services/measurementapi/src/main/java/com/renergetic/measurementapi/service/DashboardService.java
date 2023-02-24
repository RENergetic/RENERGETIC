package com.renergetic.measurementapi.service;

import com.google.gson.Gson;
import com.renergetic.measurementapi.model.Dashboard;
import com.renergetic.measurementapi.model.DashboardExt;
import com.renergetic.measurementapi.model.DashboardUnit;
import com.renergetic.measurementapi.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DashboardService {

	@Autowired
	DashboardRepository dashboardRepository;

	private final Gson gson = new Gson();
	
	public Dashboard getByGrafanaId(String grafanaId) {
		return dashboardRepository.findByGrafanaId(grafanaId);
	}

	public DashboardUnit getDashboardUnitByGrafanaId(String grafanaId){
		Dashboard dashboard = getByGrafanaId(grafanaId);

		return dashboard == null ? null : retrieveDashboardUnitFromExt(dashboard.getExt());
	}

	private DashboardUnit retrieveDashboardUnitFromExt(String ext){
		if(ext == null || ext.isEmpty())
			return null;

		DashboardExt dashboardExt = gson.fromJson(ext, DashboardExt.class);
		if(dashboardExt.getMeasurementType() != null && dashboardExt.getUnit() != null)
			return DashboardUnit.valueBySymbolAndPhysicalType(dashboardExt.getUnit(), dashboardExt.getMeasurementType()).orElse(null);
		return null;
	}
}
