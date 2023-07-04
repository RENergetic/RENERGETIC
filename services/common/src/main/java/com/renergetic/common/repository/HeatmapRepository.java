package com.renergetic.common.repository;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Heatmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("unchecked")
public interface HeatmapRepository extends JpaRepository<Heatmap, Long> {
	List<Heatmap> findByName (String name);
	
	Heatmap save(Heatmap heatmap);
	
	List<Heatmap> findByUser(Asset user);
}
