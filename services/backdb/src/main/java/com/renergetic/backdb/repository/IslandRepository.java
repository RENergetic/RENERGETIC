package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Island;

public interface IslandRepository extends JpaRepository<Island, Long> {
	List<Island> findByName (String name);
}
