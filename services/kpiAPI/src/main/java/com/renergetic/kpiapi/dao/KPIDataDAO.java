package com.renergetic.kpiapi.dao;

import com.renergetic.common.model.Domain;
import com.renergetic.kpiapi.model.KPI;

import java.util.TreeMap;

public class KPIDataDAO extends DataDAO<KPI> {
	public KPIDataDAO() {
		data = new TreeMap<>();
	}
	
	public static KPIDataDAO create(KPI kpi, Domain domain) {
		KPIDataDAO data = new KPIDataDAO();
		data.setName(kpi);
		data.setDomain(domain);
		
		return data;
	}
}
