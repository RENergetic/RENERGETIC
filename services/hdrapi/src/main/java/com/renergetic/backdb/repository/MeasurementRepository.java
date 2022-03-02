package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.Measurement;

@SuppressWarnings("unchecked")
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
	List<Measurement> findByName (String name);
	
	Measurement save(Measurement asset);
	List<Measurement> findByAssets(Asset assetId);
}
