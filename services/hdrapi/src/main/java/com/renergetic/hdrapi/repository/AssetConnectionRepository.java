package com.renergetic.hdrapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.AssetConnection;

@SuppressWarnings("unchecked")
public interface AssetConnectionRepository extends JpaRepository<AssetConnection, Long> {
	AssetConnection save(AssetConnection dashboard);
}
