package com.renergetic.backdb.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Area;
import com.renergetic.backdb.model.Heatmap;
import com.renergetic.backdb.model.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class HeatmapDAO {	
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	@JsonProperty(required = true)
	private String name;
	
	@JsonProperty(required = false)
	private String label;
	
	@JsonProperty(value = "img_url", required = false)
	private String background;
	
	@JsonProperty(required = false)
	private List<Area> areas;
	
	@JsonProperty(value = "author_id", required = false)
	private Long userId;
	
	public static HeatmapDAO create(Heatmap heatmap, List<AreaDAO> areas) {
		HeatmapDAO dao = null;
		
		if (heatmap != null) {
			dao = new HeatmapDAO();
		
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
		if (userId != null) {
			User user = new User();
			user.setId(userId);
			heatmap.setUser(user);
		}
		return heatmap;
	}
}
