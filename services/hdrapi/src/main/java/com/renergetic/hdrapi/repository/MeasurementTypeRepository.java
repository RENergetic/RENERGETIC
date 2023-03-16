package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.MeasurementType;
import org.springframework.data.jpa.repository.Query;

@SuppressWarnings("unchecked")
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
	List<MeasurementType> findByName (String name);
	
	MeasurementType save(MeasurementType asset);

	@Query("Select mt FROM MeasurementType mt WHERE mt.dashboardVisibility = true")
	List<MeasurementType> findByDashboardVisibility ();

}
