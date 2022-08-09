package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.Area;
import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Dashboard;
import com.renergetic.hdrapi.model.Heatmap;

@SuppressWarnings("unchecked")
public interface AreaRepository extends JpaRepository<Area, Long> {
	List<Area> findByName (String name);
	
	Area save(Area area);
	
	List<Area> findByDashboard(Dashboard dashboard);

	List<Area> findByAsset(Asset asset);
	
	List<Area> findByParent(Heatmap parent);
	List<Area> findByChild(Heatmap child);
}
