package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.AssetConnection;
import com.renergetic.backdb.model.Dashboard;

@SuppressWarnings("unchecked")
public interface AssetConnectionRepository extends JpaRepository<AssetConnection, Long> {
	AssetConnection save(AssetConnection dashboard);
}
