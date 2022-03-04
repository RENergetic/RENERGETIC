package com.renergetic.backdb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@RequiredArgsConstructor
@ToString
public class User {	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "uuid", nullable = true, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = true)
	private String uuid;

	@Getter
	@Setter
	@Column(name = "name", nullable = true, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = false)
	private String name;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "island_id", nullable = false, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private Asset island;

	public User(String uuid, String name) {
		super();
		this.uuid = uuid;
		this.name = name;
	}

	public void setIsland(Long id) {
		this.island = new Asset();
		this.island.setId(id);
	}	
	
}
