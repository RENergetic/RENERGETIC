package com.renergetic.backdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.AssetConnection;

@SuppressWarnings("unchecked")
public interface AssetConnectionRepository extends JpaRepository<AssetConnection, Long> {
	AssetConnection save(AssetConnection dashboard);
}
