package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.InformationTileMeasurement;
import com.renergetic.backdb.model.Measurement;

@SuppressWarnings("unchecked")
public interface InformationTileMeasurementRepository extends JpaRepository<InformationTileMeasurement, Long> {
	
	InformationTileMeasurement save(InformationTileMeasurement tile);
	
	List<InformationTileMeasurement> findByInformationTileId(Long id);
	List<InformationTileMeasurement> findByMeasurement(Measurement parent);
}
