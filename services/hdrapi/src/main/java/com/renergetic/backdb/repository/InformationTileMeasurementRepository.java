package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.Measurement;

@SuppressWarnings("unchecked")
public interface InformationTileMeasurementRepository extends JpaRepository<InformationTileMeasurement, Long> {
	
	InformationTileMeasurement save(InformationTileMeasurement tile);

	//List<InformationTileMeasurement> findByAsset(Asset asset);
	List<InformationTileMeasurement> findByMeasurement(Measurement parent);
}
