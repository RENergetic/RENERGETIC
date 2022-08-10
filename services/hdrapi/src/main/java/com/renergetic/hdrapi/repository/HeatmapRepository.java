package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.Asset;
import com.renergetic.hdrapi.model.Heatmap;

@SuppressWarnings("unchecked")
public interface HeatmapRepository extends JpaRepository<Heatmap, Long> {
	List<Heatmap> findByName (String name);
	
	Heatmap save(Heatmap heatmap);
	
	List<Heatmap> findByUser(Asset user);
}
