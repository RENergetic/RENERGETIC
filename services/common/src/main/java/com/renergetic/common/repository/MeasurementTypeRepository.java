package com.renergetic.common.repository;

import com.renergetic.common.model.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
	MeasurementType findByName (String name);
	MeasurementType findByUnit (String unit);
	
	MeasurementType save(MeasurementType asset);

	@Query("Select mt FROM MeasurementType mt WHERE mt.dashboardVisibility = true")
	List<MeasurementType> findByDashboardVisibility ();

}
