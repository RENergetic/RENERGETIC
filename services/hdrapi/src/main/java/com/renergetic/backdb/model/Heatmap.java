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
@Table(name = "heatmap")
@RequiredArgsConstructor
@ToString
public class Heatmap {	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	@Setter
	@Column(name = "name")
	@JsonProperty(required = false)
	private String name;

	@Getter
	@Setter
	@Column(name = "label")
	@JsonProperty(required = false)
	private String label;

	@Getter
	@Setter
	@Column(name = "background")
	@JsonProperty(required = false)
	private String background;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "author_id", required = false)
	private User user;
	
	public Heatmap(String name, String label, String background) {
		this.name = name;
		this.label = label;
		this.background = background;
	}

	public void setUser(Long id) {
		this.user = new User();
		this.user.setId(id);
	}	
	
}
