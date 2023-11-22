package com.renergetic.common.repository;

import com.renergetic.common.model.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings("unchecked")
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
	MeasurementType findByName (String name);
	MeasurementType findByUnit (String unit);
	
	MeasurementType save(MeasurementType asset);

	@Query("Select mt FROM MeasurementType mt WHERE mt.dashboardVisibility = true")
	List<MeasurementType> findByDashboardVisibility ();
	@Query("Select mt FROM MeasurementType mt WHERE " +
			" mt.name = :name and mt.unit = :unit and mt.physicalName = :physicalName")
	List<MeasurementType> findMeasurementType (@Param("name")String name, @Param("unit")String unit, @Param("physicalName")String physicalName);

}
