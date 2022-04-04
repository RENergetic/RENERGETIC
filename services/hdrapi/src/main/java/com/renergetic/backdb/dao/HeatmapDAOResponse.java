package com.renergetic.backdb.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Area;
import com.renergetic.backdb.model.Heatmap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class HeatmapDAOResponse {	
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	
	@JsonProperty(required = false)
	private String name;
	
	@JsonProperty(required = false)
	private String label;
	
	@JsonProperty(value = "imgUrl", required = false)
	private String background;
	
	@JsonProperty(required = false)
	private List<Area> areas;
	
	@JsonProperty(value = "author_id", required = false)
	private Long userId;
	
	public static HeatmapDAOResponse create(Heatmap heatmap, List<Area> areas) {
		HeatmapDAOResponse dao = null;
		
		if (heatmap != null) {
			dao = new HeatmapDAOResponse();
		
			dao.setId(heatmap.getId());
			dao.setName(heatmap.getName());
			dao.setLabel(heatmap.getLabel());
			dao.setBackground(heatmap.getBackground());
			
			if (heatmap.getUser() != null)
				dao.setUserId(heatmap.getUser().getId());
		}
		return dao;
	}
	
	public Heatmap mapToEntity() {
		Heatmap heatmap = new Heatmap(name, label, background);
		heatmap.setId(id);
		heatmap.setUser(userId);
		
		return heatmap;
	}
}
