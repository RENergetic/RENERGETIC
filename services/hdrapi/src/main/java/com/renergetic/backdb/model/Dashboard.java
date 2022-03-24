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
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
@RequiredArgsConstructor
@ToString
public class Dashboard {	
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
	@Pattern(regexp = "https?://\\S+([/?].+)?", message = "URL isn't valid format")
	@Column(name = "url")
	@JsonProperty(required = true)
	private String url;

	@Getter
	@Setter
	@Column(name = "label")
	@JsonProperty(required = false)
	private String label;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = true, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private User user;

	@Getter
	@Setter
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

	public void setUser(Long id) {
		this.user = new User();
		this.user.setId(id);
	}	
	
}
