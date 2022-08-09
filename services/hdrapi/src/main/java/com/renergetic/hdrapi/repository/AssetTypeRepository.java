package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.AssetType;

@SuppressWarnings("unchecked")
public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
	List<AssetType> findByName (String name);
	
	AssetType save(AssetType asset);
}
