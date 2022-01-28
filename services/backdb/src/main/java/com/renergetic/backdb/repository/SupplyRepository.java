package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Supply;

@SuppressWarnings("unchecked")
public interface SupplyRepository extends JpaRepository<Supply, Long> {
	List<Supply> findByName (String name);
	
	Supply save(Supply supply);

	@Transactional
	@Modifying
	@Query("update Supply s set s.name = :#{#supply.name}, s.description = :#{#supply.description}, s.asset.id = :#{#supply.asset.id}, s.infrastructure.id = :#{#supply.infrastructure.id}, s.power = :#{#supply.power} where s.id = :id")
	int update(@Param("supply") Supply supply, Long id);

	List<Supply> findByAssetId(String asset_id);
}
