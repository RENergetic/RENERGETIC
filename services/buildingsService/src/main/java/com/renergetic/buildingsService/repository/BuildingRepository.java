package com.renergetic.buildingsService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.buildingsService.model.Building;

public interface BuildingRepository extends JpaRepository<Building, Long>{
	List<Building> findByIdIsland (long idIsland);
	List<Building> findByName (String name);

}
