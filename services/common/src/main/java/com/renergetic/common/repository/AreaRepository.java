package com.renergetic.common.repository;

import com.renergetic.common.model.Area;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Dashboard;
import com.renergetic.common.model.Heatmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("unchecked")
public interface AreaRepository extends JpaRepository<Area, Long> {
	List<Area> findByName (String name);
	
	Area save(Area area);
	
	List<Area> findByDashboard(Dashboard dashboard);

	List<Area> findByAsset(Asset asset);
	
	List<Area> findByParent(Heatmap parent);
	List<Area> findByChild(Heatmap child);
}
