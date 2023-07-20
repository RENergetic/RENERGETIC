package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "heatmap")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Heatmap {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name")
	@JsonProperty(required = false)
	private String name;

	@Column(name = "label")
	@JsonProperty(required = false)
	private String label;

	@Column(name = "background")
	@JsonProperty(required = false)
	private String background;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(value = "author_id", required = false)
	private User user;

	@OneToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "uuid", nullable = false, insertable = true, updatable = false)
	private UUID uuid;
	
	public Heatmap(String name, String label, String background) {
		this.name = name;
		this.label = label;
		this.background = background;
	}
	
}
