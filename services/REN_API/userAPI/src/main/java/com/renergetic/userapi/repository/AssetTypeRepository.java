package com.renergetic.userapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.userapi.model.AssetType;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long>{

	Optional<AssetType> findByName(String string);

}
