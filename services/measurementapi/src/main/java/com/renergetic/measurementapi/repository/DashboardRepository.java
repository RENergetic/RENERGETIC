package com.renergetic.measurementapi.repository;

import com.renergetic.measurementapi.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
	Dashboard findByGrafanaId (String grafanaId);
}
