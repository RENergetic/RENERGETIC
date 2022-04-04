package com.renergetic.backdb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.exception.NotFoundException;
import com.renergetic.backdb.model.Area;
import com.renergetic.backdb.model.Heatmap;
import com.renergetic.backdb.repository.AreaRepository;
import com.renergetic.backdb.repository.HeatmapRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;

@Service
public class AreaService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	AreaRepository areaRepository;
	
	@Autowired
	HeatmapRepository heatmapRepository;

	// AREA CRUD OPERATIONS
	public Area save(Area area) {
		area.setId(null);
		return areaRepository.save(area);
	}
	
	public boolean deleteById(Long id) {
		if (id != null && areaRepository.existsById(id)) {
			areaRepository.deleteById(id);
			return true;
		} else throw new InvalidNonExistingIdException("The area to delete doesn't exists");
	}

	public Area update(Area area, Long id) {
		if (areaRepository.existsById(id)) {
			area.setId(id);
			return areaRepository.save(area);
		} else throw new InvalidNonExistingIdException("The area to update doesn't exists");
	}

	public List<Area> get(Map<String, String> filters, long offset, int limit) {
		Stream<Area> stream = areaRepository.findAll(new OffSetPaging(offset, limit)).stream();
		List<Area> areas;
		
		if (filters != null)
			areas = stream.filter(area -> {
				boolean equals = true;
				
				if (filters.containsKey("name"))
					equals = area.getName().equalsIgnoreCase(filters.get("name")) ||
							area.getLabel().equalsIgnoreCase(filters.get("name"));
				if (equals && filters.containsKey("asset") && area.getAsset() != null)
					equals = String.valueOf(area.getAsset().getId()).equals(filters.get("asset"));
				
				return equals;
			}).map(area -> area)
					.collect(Collectors.toList());
		else
			areas = stream
				.map(area -> area)
				.collect(Collectors.toList());
		
		if (areas.size() > 0)
			return areas;
		else throw new NotFoundException("No areas are found");
	}

	public Area getById(Long id) {
		Area area = areaRepository.findById(id).orElse(null);
		
		if (area != null)
			return area;
		else throw new NotFoundException("No area found related with id " + id);
	}

	public List<Area> getByHeatmap(Long id) {
		Heatmap heatmap = heatmapRepository.findById(id).orElse(null);
		List<Area> area = null;
		if (heatmap != null) {
			area = Stream.concat(areaRepository.findByParent(heatmap).stream(), areaRepository.findByChild(heatmap).stream())
					.collect(Collectors.toList());
		}		
		if (area != null)
			return area;
		else throw new NotFoundException("No areas found related with id " + id);
	}
}
