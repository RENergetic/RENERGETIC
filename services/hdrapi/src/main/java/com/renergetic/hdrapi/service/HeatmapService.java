package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.HeatmapDAO;
import com.renergetic.hdrapi.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Heatmap;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.repository.HeatmapRepository;
import com.renergetic.hdrapi.repository.UuidRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class HeatmapService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	HeatmapRepository heatmapRepository;
	@Autowired
	AreaService areaService;
	@Autowired
	UuidRepository uuidRepository;

	// ASSET CRUD OPERATIONS
	public HeatmapDAO save(HeatmapDAO heatmap) {
		if(heatmap.getId() !=  null && heatmapRepository.existsById(heatmap.getId()))
    		throw new InvalidCreationIdAlreadyDefinedException("Already exists a heatmap with ID " + heatmap.getId());
		
		Heatmap heatmapEntity = heatmap.mapToEntity();
		heatmapEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
		return HeatmapDAO.create(heatmapRepository.save(heatmapEntity), null);
	}
	
	public boolean deleteById(Long id) {
		if (id != null && heatmapRepository.existsById(id)) {
			heatmapRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("The heatmap to delete doesn't exists");
	}

	public HeatmapDAO update(HeatmapDAO heatmap, Long id) {
		if (heatmapRepository.existsById(id)) {
			heatmap.setId(id);
			return HeatmapDAO.create(heatmapRepository.save(heatmap.mapToEntity()), null);
		} else throw new InvalidNonExistingIdException("The heatmap to update doesn't exists");
	}

	public List<HeatmapDAO> get(Map<String, String> filters, long offset, int limit) {
		Stream<Heatmap> stream = heatmapRepository.findAll(new OffSetPaging(offset, limit)).stream();
		List<HeatmapDAO> heatmaps;
		
		if (filters != null)
			heatmaps = stream.filter(heatmap -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = heatmap.getName().equalsIgnoreCase(filters.get("name")) ||
							heatmap.getLabel().equalsIgnoreCase(filters.get("name"));
				if (filters.containsKey("background"))
					equals = heatmap.getBackground().equalsIgnoreCase(filters.get("background"));
				if (equals && filters.containsKey("user") && heatmap.getUser() != null)
					equals = String.valueOf(heatmap.getUser().getId()).equals(filters.get("user"));
				
				return equals;
			}).map(heatmap -> HeatmapDAO.create(heatmap, areaService.getByHeatmap(heatmap.getId())))
					.collect(Collectors.toList());
		else
			heatmaps = stream
				.map(heatmap -> HeatmapDAO.create(heatmap, areaService.getByHeatmap(heatmap.getId())))
				.collect(Collectors.toList());
		
		if (heatmaps.size() > 0)
			return heatmaps;
		else throw new NotFoundException("No heatmaps are found");
	}

	public HeatmapDAO getById(Long id) {
		Heatmap heatmap = heatmapRepository.findById(id).orElse(null);
		
		if (heatmap != null)
			return HeatmapDAO.create(heatmap, areaService.getByHeatmap(heatmap.getId()));
		else throw new NotFoundException("No heatmap found related with id " + id);
	}
}
