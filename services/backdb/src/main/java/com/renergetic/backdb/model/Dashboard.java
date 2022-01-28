package com.renergetic.backdb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dashboard")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Dashboard {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "name")
	@JsonProperty(required = false)
	private String name;
	
	@Pattern(regexp = "https?://\\S+([/?].+)?", message = "URL isn't valid format")
	@Column(name = "url")
	@JsonProperty(required = true)
	private String url;
	
	@Column(name = "label")
	@JsonProperty(required = false)
	private String label;
	
	@Transient
	@JsonInclude(value = Include.NON_NULL)
	@JsonProperty(access = Access.READ_ONLY)
	private Integer status;

	public Dashboard(String name, String url, String label) {
		super();
		this.name = name;
		this.url = url;
		this.label = label;
	}
	
	
}
