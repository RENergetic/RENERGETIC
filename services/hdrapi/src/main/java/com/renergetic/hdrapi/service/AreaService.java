package com.renergetic.hdrapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.hdrapi.dao.AreaDAO;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.Area;
import com.renergetic.hdrapi.model.Heatmap;
import com.renergetic.hdrapi.model.UUID;
import com.renergetic.hdrapi.repository.AreaRepository;
import com.renergetic.hdrapi.repository.HeatmapRepository;
import com.renergetic.hdrapi.repository.UuidRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

@Service
public class AreaService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	AreaRepository areaRepository;
	@Autowired
	HeatmapRepository heatmapRepository;
	@Autowired
	UuidRepository uuidRepository;

	// AREA CRUD OPERATIONS
	public AreaDAO save(AreaDAO area) {
		area.setId(null);
		Area areaEntity = area.mapToEntity();
		areaEntity.setUuid(uuidRepository.saveAndFlush(new UUID()));
		return AreaDAO.create(areaRepository.save(areaEntity));
	}
	
	public boolean deleteById(Long id) {
		if (id != null && areaRepository.existsById(id)) {
			areaRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("The area to delete doesn't exists");
	}

	public AreaDAO update(AreaDAO area, Long id) {
		if (areaRepository.existsById(id)) {
			area.setId(id);
			Area areaEntity = area.mapToEntity();
			areaEntity.setUuid(new UUID());
			return AreaDAO.create(areaRepository.save(areaEntity));
		} else throw new InvalidNonExistingIdException("The area to update doesn't exists");
	}

	public List<AreaDAO> get(Map<String, String> filters, long offset, int limit) {
		Stream<Area> stream = areaRepository.findAll(new OffSetPaging(offset, limit)).stream();
		List<AreaDAO> areas;
		
		if (filters != null)
			areas = stream.filter(area -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = area.getName().equalsIgnoreCase(filters.get("name")) ||
							area.getLabel().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("asset") && area.getAsset() != null)
					equals = String.valueOf(area.getAsset().getId()).equals(filters.get("asset"));
				
				return equals;
			}).map(area -> AreaDAO.create(area))
					.collect(Collectors.toList());
		else
			areas = stream
				.map(area -> AreaDAO.create(area))
				.collect(Collectors.toList());
		
		if (areas.size() > 0)
			return areas;
		else throw new NotFoundException("No areas are found");
	}

	public AreaDAO getById(Long id) {
		Area area = areaRepository.findById(id).orElse(null);
		
		if (area != null)
			return AreaDAO.create(area);
		else throw new NotFoundException("No area found related with id " + id);
	}

	public List<AreaDAO> getByHeatmap(Long id) {
		Heatmap heatmap = heatmapRepository.findById(id).orElse(null);
		List<AreaDAO> areas = null;
		if (heatmap != null) {
			areas = Stream.concat(areaRepository.findByParent(heatmap).stream(), areaRepository.findByChild(heatmap).stream())
					.map(area -> AreaDAO.create(area)).collect(Collectors.toList());
		}		
		if (areas != null)
			return areas;
		else throw new NotFoundException("No areas found related with id " + id);
	}
}