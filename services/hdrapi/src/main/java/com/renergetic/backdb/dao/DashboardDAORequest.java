package com.renergetic.backdb.dao;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.renergetic.backdb.model.Dashboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DashboardDAORequest {	
	@JsonProperty(required = false, access = Access.READ_ONLY)
	private Long id;

	
	@JsonProperty(required = false)
	private String name;

	@Pattern(regexp = "https?://\\S+([/?].+)?", message = "URL isn't valid format")
	@JsonProperty(required = true)
	private String url;

	
	@JsonProperty(required = false)
	private String label;
	
	@JsonProperty(required = false)
	private Long user;

	@JsonInclude(value = Include.NON_NULL)
	@JsonProperty(access = Access.READ_ONLY)
	private Integer status;

	public DashboardDAORequest(String name, String url, String label) {
		super();
		this.name = name;
		this.url = url;
		this.label = label;
	}
	
	public Dashboard mapToEntity() {
		Dashboard dashboard = new Dashboard(name, url, label);
		dashboard.setUser(user);
		dashboard.setId(id);
		
		return dashboard;
	}
}
