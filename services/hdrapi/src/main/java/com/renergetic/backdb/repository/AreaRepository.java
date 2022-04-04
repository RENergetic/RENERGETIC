package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Area;
import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Dashboard;
import com.renergetic.backdb.model.Heatmap;

@SuppressWarnings("unchecked")
public interface AreaRepository extends JpaRepository<Area, Long> {
	List<Area> findByName (String name);
	
	Area save(Area area);
	
	List<Area> findByDashboard(Dashboard dashboard);

	List<Area> findByAsset(Asset asset);
	
	List<Area> findByParent(Heatmap parent);
	List<Area> findByChild(Heatmap child);
}
