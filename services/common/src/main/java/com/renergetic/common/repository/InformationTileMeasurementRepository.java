package com.renergetic.common.repository;

import com.renergetic.common.model.InformationTileMeasurement;
import com.renergetic.common.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("unchecked")
public interface InformationTileMeasurementRepository extends JpaRepository<InformationTileMeasurement, Long> {
	
	InformationTileMeasurement save(InformationTileMeasurement tile);
	
	List<InformationTileMeasurement> findByInformationTileId(Long id);
	List<InformationTileMeasurement> findByMeasurement(Measurement parent);
}
