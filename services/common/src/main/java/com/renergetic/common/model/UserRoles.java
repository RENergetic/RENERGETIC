package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class UserRoles {	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "uuid", nullable = true, insertable = true, updatable = true, unique = true)
	@JsonProperty(required = true)
	private String uuid;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, insertable = true, updatable = true, unique = false)
	@JsonProperty(required = false)
	private RoleType type;

	@Column(name = "update_date", nullable = false, insertable = true, updatable = true, unique = false)
	@JsonProperty(required = false)
	private LocalDateTime updateDate;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_id", nullable = false, insertable = true, updatable = true)
	@JsonProperty(required = false)
	private User user;

	public UserRoles(String uuid, RoleType type, LocalDateTime date) {
		super();
		this.uuid = uuid;
		this.type = type;;
		this.updateDate = date;
	}
	
}
