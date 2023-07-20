package com.renergetic.common.repository;

import com.renergetic.common.model.AssetConnection;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
public interface AssetConnectionRepository extends JpaRepository<AssetConnection, Long> {
	AssetConnection save(AssetConnection dashboard);
}
