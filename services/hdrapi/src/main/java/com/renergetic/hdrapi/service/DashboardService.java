package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.DashboardDAO;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Dashboard;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.model.User;
import com.renergetic.hdrapi.repository.DashboardRepository;
import com.renergetic.hdrapi.repository.UserRepository;
import com.renergetic.hdrapi.repository.UuidRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class DashboardService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	DashboardRepository dashboardRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UuidRepository uuidRepository;

	// AREA CRUD OPERATIONS
	public DashboardDAO save(DashboardDAO dashboard) {
		dashboard.setId(null);
		Dashboard dashboardEntity = dashboard.mapToEntity();
		dashboardEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
		return DashboardDAO.create(dashboardRepository.save(dashboardEntity));
	}
	
	public boolean deleteById(Long id) {
		if (id != null && dashboardRepository.existsById(id)) {
			dashboardRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("The dashboard to delete doesn't exists");
	}

	public DashboardDAO update(DashboardDAO dashboard, Long id) {
		if (dashboardRepository.existsById(id)) {
			dashboard.setId(id);
			return DashboardDAO.create(dashboardRepository.save(dashboard.mapToEntity()));
		} else throw new InvalidNonExistingIdException("The dashboard to update doesn't exists");
	}

	public List<DashboardDAO> get(Map<String, String> filters, long offset, int limit) {
		Stream<Dashboard> stream = dashboardRepository.findAll(new OffSetPaging(offset, limit)).stream();
		List<DashboardDAO> dashboards;
		
		if (filters != null)
			dashboards = stream.filter(dashboard -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = dashboard.getName().equalsIgnoreCase(filters.get("name")) ||
							dashboard.getLabel().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("grafana_id"))
					equals = String.valueOf(dashboard.getGrafanaId()).equalsIgnoreCase(filters.get("grafana_id"));
				if (equals && filters.containsKey("ext"))
					equals = String.valueOf(dashboard.getExt()).equalsIgnoreCase(filters.get("ext"));
				if (equals && filters.containsKey("user") && dashboard.getUser() != null)
					equals = String.valueOf(dashboard.getUser().getId()).equals(filters.get("user"));
				
				return equals;
			}).map(dashboard -> DashboardDAO.create(dashboard))
					.collect(Collectors.toList());
		else
			dashboards = stream
				.map(dashboard -> DashboardDAO.create(dashboard))
				.collect(Collectors.toList());
		
		if (dashboards.size() > 0)
			return dashboards;
		else throw new NotFoundException("No dashboards are found");
	}

	public DashboardDAO getById(Long id) {
		Dashboard dashboard = dashboardRepository.findById(id).orElse(null);
		
		if (dashboard != null)
			return DashboardDAO.create(dashboard);
		else throw new NotFoundException("No dashboard found related with id " + id);
	}

	public List<DashboardDAO> getByUser(Long id) {
		User user = userRepository.findById(id).orElse(null);
		
		if (user != null) {
			List<Dashboard> dashboards = dashboardRepository.findByUser(user);
			
			if (dashboards != null) 
				return dashboards.stream()
						.map((dashboard) -> DashboardDAO.create(dashboard))
						.collect(Collectors.toList());
			else throw new NotFoundException("No dashboards found related with given user id " + id);
		} else throw new NotFoundException("No users found related with id " + id);
	}

	public List<DashboardDAO> getAvailableToUserId(Long userId, Long offset, Integer limit){
		User user = userRepository.findById(userId).orElse(null);
		if(user == null)
			throw new NotFoundException("No users found related with id " + userId);

		List<Dashboard> dashboards = dashboardRepository.findAvailableForUserId(userId, offset, limit);
		if (dashboards != null)
			return dashboards.stream()
					.map(DashboardDAO::create)
					.collect(Collectors.toList());
		else throw new NotFoundException("No dashboards found related with given user id " + userId);
	}
}
