package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Demand;

@SuppressWarnings("unchecked")
public interface DemandRepository extends JpaRepository<Demand, Long> {
	List<Demand> findByName (String name);
	
	Demand save(Demand demand);

	@Transactional
	@Modifying
	@Query("update Demand d set d.name = :#{#demand.name}, d.description = :#{#demand.description}, d.assetId = :#{#demand.assetId}, d.outputInfrastructureId = :#{#demand.outputInfrastructureId}, d.power = :#{#demand.power} where d.id = :id")
	int update(@Param("demand") Demand demand, Long id);

	List<Demand> findByAssetId(String asset_id);
}
