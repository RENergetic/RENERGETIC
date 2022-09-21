package com.renergetic.hdrapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.AssetType;

@SuppressWarnings("unchecked")
public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
	Optional<AssetType> findByName (String name);
	
	AssetType save(AssetType asset);
}
