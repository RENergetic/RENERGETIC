package com.renergetic.backdb.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "user_roles")
@RequiredArgsConstructor
@ToString
public class UserRoles {	
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
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = false)
	private RoleType type;

	@Getter
	@Setter
	@Column(name = "update_date", nullable = false, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = false)
	private LocalDateTime update_date;

	@Getter
	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = false, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private User user;

	public UserRoles(String uuid, RoleType type, LocalDateTime date) {
		super();
		this.uuid = uuid;
		this.type = type;;
		this.update_date = date;
	}

	public void setUser(Long id) {
		this.user = new User();
		this.user.setId(id);
	}	
	
}
