package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Island;

@SuppressWarnings("unchecked")
public interface IslandRepository extends JpaRepository<Island, Long> {
	List<Island> findByName (String name);

	Island save(Island island);
	
	@Transactional
	@Modifying
	@Query("update Island i set i.name = :#{#island.name}, i.location = :#{#island.location} where i.id = :id")
	int update(@Param("island") Island island, Long id);

	List<Island> findByLocation(String location);
}
