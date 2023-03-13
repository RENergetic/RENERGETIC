package com.renergetic.measurementapi.service;

import com.google.gson.Gson;
import com.renergetic.measurementapi.model.Dashboard;
import com.renergetic.measurementapi.model.DashboardExt;
import com.renergetic.measurementapi.model.MeasurementType;
import com.renergetic.measurementapi.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

	@Autowired
	DashboardRepository dashboardRepository;

	@Autowired
	MeasurementTypeService measurementTypeService;

	private final Gson gson = new Gson();
	
	public Dashboard getByGrafanaId(String grafanaId) {
		return dashboardRepository.findByGrafanaId(grafanaId);
	}

	public MeasurementType getDashboardUnitByGrafanaId(String grafanaId){
		Dashboard dashboard = getByGrafanaId(grafanaId);

		return dashboard == null ? null : retrieveDashboardUnitFromExt(dashboard.getExt());
	}

	private MeasurementType retrieveDashboardUnitFromExt(String ext){
		if(ext == null || ext.isEmpty())
			return null;

		try{
			DashboardExt dashboardExt = gson.fromJson(ext, DashboardExt.class);
			return dashboardExt.getMeasurementType();
		} catch (Exception e){
			return null;
		}
	}
}
