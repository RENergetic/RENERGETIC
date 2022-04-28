package com.renergetic.backdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.renergetic.backdb.model.Dashboard;
import com.renergetic.backdb.model.User;

@SuppressWarnings("unchecked")
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
	List<Dashboard> findByName (String name);
	
	Dashboard save(Dashboard dashboard);

	List<Dashboard> findByUrl(String location);
	
	List<Dashboard> findByUser(User user);
}
