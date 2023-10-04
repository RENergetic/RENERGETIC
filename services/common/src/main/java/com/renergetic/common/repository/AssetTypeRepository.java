package com.renergetic.common.repository;

import com.renergetic.common.model.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
	Optional<AssetType> findByName (String name);
	
	AssetType save(AssetType asset);
}
