package com.renergetic.hdrapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.hdrapi.model.Dashboard;
import com.renergetic.hdrapi.model.User;
import org.springframework.data.jpa.repository.Query;

@SuppressWarnings("unchecked")
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
	List<Dashboard> findByName (String name);

	Dashboard save(Dashboard dashboard);

	List<Dashboard> findByUrl(String location);

	List<Dashboard> findByUser(User user);

	//	@Query(value = "SELECT dashboard.* " +
//			"FROM (asset asset_conn " +
//			"INNER JOIN asset_connection ON asset_connection.connected_asset_id = asset_conn.id " +
//			"INNER JOIN asset asset_user ON asset_user.id = asset_connection.asset_id AND asset_user.user_id = :userId " +
//			"INNER JOIN asset_dashboard ON asset_dashboard.asset_id = asset_conn.id " +
//			"INNER JOIN dashboard ON dashboard.id = asset_dashboard.dashboard_id) " +
//			"LIMIT :limit OFFSET :offset ;", nativeQuery = true)
	@Query(value = " SELECT distinct  dashboard.* FROM " +
			" ( dashboard " +
			" 	LEFT JOIN asset asset_dashboard " +
			" 		ON dashboard.id = asset_dashboard.id or dashboard.user_id is null  or dashboard.user_id = :userId "+
			" 	LEFT JOIN asset_connection asset_conn ON asset_dashboard.id = asset_conn.connected_asset_id " +
			" 	LEFT JOIN asset asset_user ON asset_user.id = asset_conn.asset_id and asset_user.user_id = :userId " +
			" ) LIMIT :limit OFFSET :offset ;", nativeQuery = true)
	List<Dashboard> findAvailableForUserId(long userId, long offset, int limit);
}
