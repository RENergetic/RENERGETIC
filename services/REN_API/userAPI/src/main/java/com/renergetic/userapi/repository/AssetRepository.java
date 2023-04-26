package com.renergetic.userapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.renergetic.userapi.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long>{

	@Query("SELECT asset FROM Asset asset "
			+ "WHERE asset.user.id = :id and asset.type.name = 'user'")
	Optional<Asset> findByUserId(Long id);

}
