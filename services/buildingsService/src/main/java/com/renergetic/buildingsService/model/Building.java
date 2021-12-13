package com.renergetic.buildingsService.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "building")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Building {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "id_island")
	private long idIsland;

	public Building(String name, String description, long idIsland) {
		super();
		this.name = name;
		this.description = description;
		this.idIsland = idIsland;
	}
}
