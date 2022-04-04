package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Heatmap;

@SuppressWarnings("unchecked")
public interface HeatmapRepository extends JpaRepository<Heatmap, Long> {
	List<Heatmap> findByName (String name);
	
	Heatmap save(Heatmap heatmap);
	
	List<Heatmap> findByUser(Asset user);
}
