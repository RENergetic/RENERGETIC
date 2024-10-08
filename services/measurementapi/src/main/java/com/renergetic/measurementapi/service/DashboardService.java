package com.renergetic.measurementapi.service;

import com.renergetic.common.model.Dashboard;
import com.renergetic.common.model.MeasurementType;
import com.renergetic.common.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

	@Autowired
	DashboardRepository dashboardRepository;

	@Autowired
	MeasurementTypeService measurementTypeService;
	
	public Dashboard getByGrafanaId(String grafanaId) {
		return dashboardRepository.findByGrafanaId(grafanaId);
	}

	public MeasurementType getDashboardUnitByGrafanaId(String grafanaId){
		Dashboard dashboard = getByGrafanaId(grafanaId);

		return dashboard == null ? null : dashboard.getMeasurementType();
	}

	/*private MeasurementType retrieveDashboardUnitFromExt(String ext){
		if(ext == null || ext.isEmpty())
			return null;

		try{
			DashboardExt dashboardExt = gson.fromJson(ext, DashboardExt.class);
			return dashboardExt.getMeasurementType();
		} catch (Exception e){
			return null;
		}
	}*/
}
